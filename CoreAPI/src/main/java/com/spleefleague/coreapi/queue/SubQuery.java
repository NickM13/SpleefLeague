package com.spleefleague.coreapi.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 9/16/2020
 */
public class SubQuery {

    public String type;
    public List<String> values;
    public boolean hasStar;

    SubQuery(String section) {
        String[] splits = section.split(":");
        this.type = splits[0];
        this.values = new ArrayList<>();
        for (String s : splits[1].split(",")) {
            if (s.equals("*")) {
                this.hasStar = true;
            } else {
                this.values.add(s);
            }
        }
    }

    public static SubQuery[] splitQuery(String query) {
        String[] sections = query.split(";");
        SubQuery[] subQueries = new SubQuery[sections.length];
        for (int i = 0; i < sections.length; i++) {
            subQueries[i] = new SubQuery(sections[i]);
        }
        return subQueries;
    }

    /**
     * Both sub queries should be either whitelist or blacklist (with or without star)
     *
     * @param query1 Sub Query
     * @param query2 Sub Query
     * @return
     */
    private static String mergeSame(SubQuery query1, SubQuery query2) {
        StringBuilder merged = new StringBuilder();
        for (String v1 : query1.values) {
            for (String v2 : query2.values) {
                if (v1.equals(v2)) {
                    if (merged.length() > 0) {
                        merged.append(",");
                    }
                    merged.append(v1);
                    break;
                }
            }
        }
        return merged.toString();
    }

    /**
     * Takes a whitelist and blacklist of values and produces a new string
     *
     * @param whitelist Whitelist Sub Query
     * @param blacklist Blacklist Sub Query
     * @return
     */
    private static String mergeOther(SubQuery whitelist, SubQuery blacklist) {
        StringBuilder merged = new StringBuilder();
        for (String v1 : whitelist.values) {
            boolean ignored = false;
            for (String v2 : blacklist.values) {
                if (v1.equals(v2)) {
                    ignored = true;
                    break;
                }
            }
            if (!ignored) {
                if (merged.length() > 0)
                    merged.append(",");
                merged.append(v1);
            }
        }
        return merged.toString();
    }

    /**
     * Compare two sub queries of the same type, returning a new query string
     * (empty string should exit player match)
     *
     * @param that Sub Query
     * @return New Query String
     */
    public String compareValue(SubQuery that) {
        String newVal;
        if (hasStar) {
            if (that.hasStar) {
                String str = mergeSame(this, that);
                newVal = "*" + (str.length() > 0 ? "," : "") + str;
            } else {
                newVal = mergeOther(that, this);
            }
        } else {
            if (that.hasStar) {
                newVal = mergeOther(this, that);
            } else {
                newVal = mergeSame(this, that);
            }
        }
        return newVal;
    }

}
