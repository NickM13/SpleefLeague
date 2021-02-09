package com.spleefleague.proxycore.droplet;

import com.spleefleague.coreapi.utils.packet.bungee.server.PacketBungeeServerKill;
import com.spleefleague.coreapi.utils.packet.bungee.server.PacketBungeeServerPing;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public class DropletManager {

    private class AwaitingServer {

        private final ServerInfo serverInfo;
        private final DropletType type;
        private final String serverName;
        private final int id;
        private final String hostName;
        private final int port;
        private int timeoutsRemaining;
        private boolean connected;

        public AwaitingServer(DropletType type, int id, String hostName, int port) {
            this.type = type;
            this.id = id;
            this.hostName = hostName;
            this.port = port;
            this.timeoutsRemaining = 5;
            System.out.println("STARTING: " + hostName + ":" + port);
            this.serverName = type.name().toLowerCase() + "_" + id;
            this.serverInfo = ProxyCore.getInstance().getProxy().constructServerInfo(
                    serverName,
                    new InetSocketAddress(hostName, port),
                    "",
                    type.restricted);
        }

        public void ping() {
            serverInfo.ping((serverPing, throwable) -> {
                if (serverPing != null && !connected) {
                    System.out.println("Server " + serverName + " successfully started!");
                    Droplet droplet = new Droplet(serverName, hostName, port, type, serverInfo);
                    onDropletConnect(droplet);
                    connected = true;
                } else {
                    timeoutsRemaining--;
                }
            });
        }

        public boolean isConnected() {
            return connected;
        }

        public boolean isTimedOut() {
            return timeoutsRemaining < 0;
        }

        public int getPort() {
            return port;
        }

    }

    private final Map<DropletType, Set<Droplet>> ACTIVE_SERVERS_TYPED = new HashMap<>();
    private final Map<String, Set<Droplet>> ACTIVE_SERVERS = new HashMap<>();
    private final Set<Droplet> DROPLETS = new HashSet<>();

    private final Map<DropletType, Integer> NEXT_DROPLET = new HashMap<>();
    private final Map<String, Integer> NEXT_PORT = new HashMap<>();
    private final Map<DropletType, Set<Integer>> USED_IDS = new HashMap<>();
    private final Map<String, Set<Integer>> USED_PORTS = new HashMap<>();

    private String CMD = "";
    private String PAPER_DIR = "paper/";
    private String DROPLET_CREATOR = "";
    private Integer PORT_START = 25568, PORT_END = 25800;
    private List<String> HOSTS = new ArrayList<>();

    private final Map<DropletType, List<AwaitingServer>> AWAITING = new HashMap<>();

    private final Map<DropletType, Integer> DROP_COUNTER = new HashMap<>();

    private String mainHost;

    private File file;
    private Configuration configuration;

    private boolean enabled = false;

    public void init() {
        try {
            Properties dropletProps = new Properties();
            String dropletPath = System.getProperty("user.dir") + "/../droplet.cfg";
            FileInputStream file = new FileInputStream(dropletPath);

            dropletProps.load(file);
            file.close();

            if (!dropletProps.getProperty("enabled", "false").equals("true")) {
                System.out.println("Droplets are not currently enabled");
                System.out.println("To enable, set enabled=true in droplets.cfg");
                return;
            }

            for (DropletType type : DropletType.values()) {
                NEXT_DROPLET.put(type, 0);
                ACTIVE_SERVERS_TYPED.put(type, new HashSet<>());
                AWAITING.put(type, new ArrayList<>());
                DROP_COUNTER.put(type, 0);
                USED_IDS.put(type, new HashSet<>());
            }

            CMD = dropletProps.getProperty("command", "");
            PAPER_DIR = dropletProps.getProperty("root", PAPER_DIR);
            DROPLET_CREATOR = dropletProps.getProperty("creator");
            String portStart = dropletProps.getProperty("portStart", "25568");
            String portEnd = dropletProps.getProperty("portEnd", "25900");
            PORT_START = Integer.parseInt(portStart);
            PORT_END = Integer.parseInt(portEnd);

            String hosts = dropletProps.getProperty("hosts");
            for (String host : hosts.split(",")) {
                ACTIVE_SERVERS.put(host, new HashSet<>());
                USED_PORTS.put(host, new HashSet<>());
                NEXT_PORT.put(host, PORT_START);
                HOSTS.add(host);
            }
            mainHost = HOSTS.get(0);
        } catch (FileNotFoundException e) {
            System.out.println("droplet.cfg not found in folder before waterfall");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::pingAwaiting, 10, 20, TimeUnit.SECONDS);
        initConfigServers();
        initReconnectHandler();

        enabled = true;
    }

    private void initConfigServers() {
        try {
            file = new File(ProxyCore.getInstance().getProxy().getPluginsFolder() + "/../config.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            configuration.set("servers", null);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, ServerInfo> entry : ProxyServer.getInstance().getServersCopy().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("lobby")) {
                ProxyServer.getInstance().getServers().remove(entry.getKey());
                continue;
            }
            InetSocketAddress address = (InetSocketAddress) entry.getValue().getSocketAddress();
            DropletType type = DropletType.valueOf(entry.getKey().split("_")[0].toUpperCase());
            int id = Integer.parseInt(entry.getKey().split("_")[1]);
            String hostName = address.getHostName();
            int port = address.getPort();
            AwaitingServer awaiting = new AwaitingServer(type, id, hostName, port);
            awaiting.timeoutsRemaining = 0;
            ProxyServer.getInstance().getServers().remove(entry.getKey());
            configuration.set("servers." + entry.getKey(), null);
            USED_IDS.get(type).add(id);
            USED_PORTS.get(mainHost).add(port);
            AWAITING.get(type).add(awaiting);
            System.out.println("Attempting to reconnect to " + hostName + ":" + port);
        }
        pingAwaiting();
    }

    public boolean isEnabled() {
        return enabled;
    }

    private void updatePriorities() {
        List<String> priorities;
        if (ACTIVE_SERVERS_TYPED.get(DropletType.LOBBY).isEmpty()) {
            priorities = DROPLETS.stream().map(Droplet::getName).collect(Collectors.toList());
        } else {
            priorities = ACTIVE_SERVERS_TYPED.get(DropletType.LOBBY).stream().map(Droplet::getName).collect(Collectors.toList());
        }

        ArrayList<LinkedHashMap<String, Object>> listeners = (ArrayList<LinkedHashMap<String, Object>>) configuration.getList("listeners");
        LinkedHashMap<String, Object> listener = listeners.get(0);

        listener.put("priorities", priorities);

        listeners.set(0, listener);
        configuration.set("listeners", listeners);
    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDropletDisconnect(Droplet droplet) {
        ACTIVE_SERVERS_TYPED.get(droplet.getType()).remove(droplet);
        ACTIVE_SERVERS.remove(droplet.getHost()).remove(droplet);
        DROPLETS.remove(droplet);

        configuration.set("servers." + droplet.getName(), null);

        updatePriorities();

        saveConfig();
    }

    private void onDropletConnect(Droplet droplet) {
        ACTIVE_SERVERS_TYPED.get(droplet.getType()).add(droplet);
        ACTIVE_SERVERS.get(droplet.getHost()).add(droplet);
        DROPLETS.add(droplet);

        ProxyCore.getInstance().getProxy().getServers().put(droplet.getName(), droplet.getInfo());

        configuration.set("servers." + droplet.getName() + ".motd", droplet.getType().getName() + "Droplet Server");
        configuration.set("servers." + droplet.getName() + ".address", droplet.getHost() + ":" + droplet.getPort());
        configuration.set("servers." + droplet.getName() + ".restricted", droplet.getType().restricted);

        updatePriorities();

        saveConfig();
    }

    private void initReconnectHandler() {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandler() {
            @Override
            public ServerInfo getServer(ProxiedPlayer proxiedPlayer) {
                Droplet droplet = getAvailable(DropletType.LOBBY);
                if (droplet != null) {
                    return droplet.getInfo();
                }
                return null;
            }

            @Override
            public void setServer(ProxiedPlayer proxiedPlayer) {

            }

            @Override
            public void save() {

            }

            @Override
            public void close() {

            }
        });
    }

    // After how many checks to drop servers
    private static final int DROP_AT = 30;

    private void pingAwaiting() {
        int totalPlayers = ProxyCore.getInstance().getPlayers().getAll().size() + 1;
        for (DropletType type : DropletType.values()) {
            int totalServers = AWAITING.get(type).size() + ACTIVE_SERVERS_TYPED.get(type).size();
            if (totalServers < totalPlayers / type.playersPerServer) {
                System.out.println("Attempting to open 2 " + type.getName() + " servers...");
                openNext(type, 2);
            } else if (totalServers * 0.7D > totalPlayers / type.playersPerServer) {
                if (DROP_COUNTER.get(type) >= DROP_AT) {
                    System.out.println("Dropping extra server for type " + type.getName() + "...");
                    dropServer(type, 1);
                } else {
                    System.out.println("DEBUG: Extra servers for type " + type.getName() + " detected, attempting to shut down after " + (DROP_AT - DROP_COUNTER.get(type)) + " more checks");
                    DROP_COUNTER.put(type, DROP_COUNTER.get(type) + 1);
                }
                continue;
            }
            DROP_COUNTER.put(type, 0);
        }

        for (Map.Entry<DropletType, List<AwaitingServer>> entry : AWAITING.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            System.out.println("Waiting for " + entry.getValue().size() + " servers to startup for type " + entry.getKey());
            Iterator<AwaitingServer> it = entry.getValue().iterator();
            while (it.hasNext()) {
                AwaitingServer awaiting = it.next();
                if (awaiting.isConnected() || awaiting.isTimedOut()) {
                    System.out.println("Connected or timed out server found, removed from list");
                    it.remove();
                } else {
                    awaiting.ping();
                }
            }
        }
    }

    public void close() {
        //ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeServerKill());
        //ProxyCore.getInstance().getPacketManager().flush();
    }

    public Droplet getAvailable(DropletType type) {
        if (!enabled) return null;

        Droplet bestDroplet = null;
        int bestSize = 0;

        for (Droplet droplet : ACTIVE_SERVERS_TYPED.get(type)) {
            if (bestDroplet == null || droplet.getInfo().getPlayers().size() < bestSize) {
                bestDroplet = droplet;
                bestSize = droplet.getInfo().getPlayers().size();
            }
            if (bestDroplet.getInfo().getPlayers().size() < type.softCap) {
                return bestDroplet;
            }
        }
        if (bestDroplet == null) return null;
        if (bestSize > type.softCap) {
            // We need more servers!
            // This is handled in pingAwaiting
        }
        return bestDroplet;
    }

    private class FriendDroplet {

        Droplet droplet;
        int friendCount = 0;

        public FriendDroplet(Droplet droplet) {
            this.droplet = droplet;
        }

        public int getFriendCount() {
            return friendCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FriendDroplet that = (FriendDroplet) o;
            return Objects.equals(droplet, that.droplet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(droplet);
        }
    }

    public Droplet getBestLobby(ProxyCorePlayer cp) {
        Set<FriendDroplet> friendDroplets = new TreeSet<>(Comparator.comparingInt(FriendDroplet::getFriendCount));

        for (ProxyCorePlayer friend : cp.getFriends().getOnline()) {
            Droplet droplet = friend.getCurrentDroplet();
            if (droplet.getType() == DropletType.LOBBY) {
                FriendDroplet friendDroplet = new FriendDroplet(droplet);
                friendDroplets.add(friendDroplet);
            }
        }

        for (FriendDroplet friendDroplet : friendDroplets) {
            if (friendDroplet.droplet.getInfo().getPlayers().size() < DropletType.LOBBY.softCap * 1.5 ) {
                return friendDroplet.droplet;
            }
        }

        return getAvailable(DropletType.LOBBY);
    }

    public void pingAll() {
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeServerPing());
    }

    public Integer getNextOpenPort(String host) {
        for (int i = PORT_START; i < PORT_END; i++) {
            Set<Integer> ports = USED_PORTS.get(host);
            if (!ports.contains(i)) {
                return i;
            }
        }
        return null;
    }

    public void openNext(DropletType type) {
        try {
            Socket socket = new Socket("127.0.0.1", 3000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int nextPort = NEXT_PORT.get(mainHost);
            while (USED_PORTS.get(mainHost).contains(nextPort)) {
                nextPort++;
                if (nextPort > PORT_END) {
                    nextPort = PORT_START;
                }
            }
            int nextId = NEXT_DROPLET.get(type);
            while (USED_IDS.get(type).contains(nextId)) {
                nextId++;
            }
            USED_PORTS.get(mainHost).add(nextPort);
            USED_IDS.get(type).add(nextId);

            nextPort++;
            if (nextPort > PORT_END) {
                nextPort = PORT_START;
            }
            NEXT_PORT.put(mainHost, nextPort);
            NEXT_DROPLET.put(type, nextId + 1);

            out.println("type:" + type.name().toLowerCase());
            out.println("id:" + nextId);
            out.println("port:" + String.valueOf(nextPort));
            out.println("count:" + 1);

            AwaitingServer awaitingServer = new AwaitingServer(type, nextId, mainHost, nextPort);

            AWAITING.get(type).add(awaitingServer);

            String returnStr = in.readLine();
            //System.out.println(returnStr);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dropServer(DropletType type) {
        Iterator<Droplet> it = ACTIVE_SERVERS.get(mainHost).iterator();
        Droplet toDrop = null;
        int bestSize = 0;
        while (it.hasNext()) {
            Droplet droplet = it.next();
            if (droplet.getInfo().getPlayers().isEmpty()) {
                toDrop = droplet;
                break;
            }
            if (toDrop == null || droplet.getInfo().getPlayers().size() < bestSize) {
                toDrop = droplet;
                bestSize = toDrop.getInfo().getPlayers().size();
            }
        }
        if (toDrop == null) {
            ProxyCore.getInstance().getLogger().warning("No servers of type " + type.name() + " to drop");
            return;
        }
        ProxyCore.getInstance().getPacketManager().sendPacket(toDrop.getInfo(), new PacketBungeeServerKill());
        ProxyCore.getInstance().getPacketManager().flush();
        synchronized (ACTIVE_SERVERS) {
            ACTIVE_SERVERS.get(mainHost).remove(toDrop);
        }
        synchronized (ACTIVE_SERVERS_TYPED) {
            ACTIVE_SERVERS_TYPED.get(type).remove(toDrop);
        }
    }

    public void openNext(DropletType type, int count) {
        for (int i = 0; i < count; i++) {
            openNext(type);
        }
    }

    public void dropServer(DropletType type, int count) {
        for (int i = 0; i < count; i++) {
            dropServer(type);
        }
    }

    /*
    private void addServer(DropletType type, ServerInfo serverInfo, String host, String port) {
        Droplet droplet = new Droplet(host, Integer.parseInt(port), type, serverInfo);

        if (!ACTIVE_SERVERS.containsKey(host)) {
            ACTIVE_SERVERS.put(host, new HashSet<>());
        }
        ACTIVE_SERVERS.get(host).add(droplet);
        ACTIVE_SERVERS_TYPED.get(type).add(droplet);
    }

    public void onPingReceive(DropletType type, SocketAddress socketAddress) {
        ServerInfo serverInfo = ProxyCore.getInstance().getProxy().constructServerInfo(
                type.name() + NEXT_DROPLET.get(type),
                socketAddress,
                "",
                type.restricted);

        String socketIp = socketAddress.toString();
        String[] split = socketIp.split(":");
        String host = split[0];
        String port = split[1];

        addServer(type, serverInfo, host, port);
    }
    */

}
