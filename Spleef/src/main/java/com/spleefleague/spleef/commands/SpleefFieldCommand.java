package com.spleefleague.spleef.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.spleef.field.Field;
import com.spleefleague.spleef.field.FieldBuilder;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class SpleefFieldCommand extends CommandTemplate {

    public SpleefFieldCommand() {
        super(SpleefFieldCommand.class, "spleeffield", Rank.DEVELOPER);
        setUsage("ehh idk");
        setDescription("Yea yea Ill get to this");
        setOptions("fields", cp -> Field.getFieldNames());
    }

    @CommandAnnotation
    public void spleefField(CorePlayer sender) {
        success(sender, "Spleef Field woooo!");
    }
    
    @CommandAnnotation
    public void spleefFieldCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<fieldName>") String fieldName) {
        FieldBuilder.createField(sender, fieldName);
    }
    
    @CommandAnnotation
    public void spleefFieldEdit(CorePlayer sender,
            @LiteralArg(value="edit") String l,
            @OptionArg(listName="fields") String fieldName) {
        FieldBuilder.editField(sender, fieldName);
    }
    
    @CommandAnnotation
    public void spleefFieldSave(CorePlayer sender,
            @LiteralArg(value="save") String l) {
        FieldBuilder.saveField(sender);
    }
    
    @CommandAnnotation
    public void spleefFieldLeave(CorePlayer sender,
            @LiteralArg(value="leave") String l) {
        FieldBuilder.leaveField(sender);
    }

}
