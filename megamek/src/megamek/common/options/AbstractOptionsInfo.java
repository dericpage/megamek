/*
 * MegaMek - Copyright (C) 2000-2003 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

package megamek.common.options;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Abstract base class for Singletons representing Options' static information
 * such as displayable name, description etc. The derived classes must implement
 * the Singleton pattern
 */
public class AbstractOptionsInfo implements IOptionsInfo {

    private final static String GROUP_SUFFIX = ".group."; //$NON-NLS-1$
    private final static String OPTION_SUFFIX = ".option."; //$NON-NLS-1$
    private final static String DISPLAYABLE_NAME_SUFFIX = ".displayableName"; //$NON-NLS-1$
    private final static String DESCRIPTION_SUFFIX = ".description"; //$NON-NLS-1$

    /**
     * The OptionsInfo name that must be unique. Every instance of the
     * AbstractOptionsInfo must have unique name, it's used to query the NLS
     * dependent information from the common resource bundle.
     *
     * @see #getOptionDisplayableName(String)
     * @see #getGroupDisplayableName(String)
     * @see #getOptionDescription(String)
     */
    private final String name;

    /**
     * Hashtable of the <code>OptionInfo</code> used to store/find option
     * info.
     */
    private final Hashtable<String, OptionInfo> optionsHash = new Hashtable<>();

    /**
     * List of option groups. The order of groups is important. The first group
     * added by <code>addGroup</code> is the first in the
     * <code>Enumeration</code> returned by <code>getGroups</code>
     */
    private final Vector<IBasicOptionGroup> groups = new Vector<>();

    /**
     * Flag that indicates that this filling the the options info data is
     * completed. <code>addGroup</code> and <code>addOptionInfo</code> will
     * have no effect if it's <code>true</code>
     *
     * @see #finish()
     * @see #addGroup(String)
     * @see #addOptionInfo(IBasicOptionGroup, String)
     */
    private boolean finished;

    /**
     * The <code>HashSet</code> used to check if the options info is already
     * registered
     * 
     * @see AbstractOptionsInfo()
     */
    private static final HashSet<String> NAMES = new HashSet<>();

    /**
     * Protected constructor. It is called only by descendants. The name must be
     * unique because it's used to query the NLS dependent information from the
     * resource bundle.
     * 
     * @param name options info name
     */
    AbstractOptionsInfo(final String name) {
        if (NAMES.contains(name)) {
            throw new IllegalArgumentException(
                    "OptionsInfo '" + name + "' is already registered"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        NAMES.add(name);
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see megamek.common.options.IOptionsInfo#getOptionInfo(java.lang.String)
     */
    public IOptionInfo getOptionInfo(final String name) {
        return optionsHash.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see megamek.common.options.IOptionsInfo#getGroups()
     */
    public Enumeration<IBasicOptionGroup> getGroups() {
        return groups.elements();
    }

    IBasicOptionGroup addGroup(final String name) {
        return addGroup(name, null);
    }

    IBasicOptionGroup addGroup(final String name,
                               final String key) {
        IBasicOptionGroup group = null;
        if (!finished) {
            for (int i = 0; i < groups.size(); i++) {
                final IBasicOptionGroup g = groups.elementAt(i);
                if (null != g && g.getName().equals(name)) {
                    group = groups.elementAt(i);
                    break;
                }
            }
            if (null == group) {
                group = (null == key ? new OptionGroup(name) : new OptionGroup(
                        name, key));
                groups.addElement(group);
            }
        }
        return group;
    }

    void addOptionInfo(final IBasicOptionGroup group,
                       final String name) {
        if (!finished) {
            // TODO: I'm not happy about this cast but this is better than it
            // was before.
            ((OptionGroup) group).addOptionName(name);
            setOptionInfo(name, new OptionInfo(name));
        }
    }

    /**
     * Returns the user friendly NLS dependent name suitable for displaying in
     * the options editor dialogs etc.
     * 
     * @param groupName
     * @return group displayable name
     */
    String getGroupDisplayableName(final String groupName) {
        for (int i = 0; i < groups.size(); i++) {
            final IBasicOptionGroup g = groups.elementAt(i);
            if (null != g && g.getName().equals(groupName)) {
                return Messages.getString(name + GROUP_SUFFIX + groupName
                                          + DISPLAYABLE_NAME_SUFFIX);
            }
        }
        return null;
    }

    /**
     * Records that filling of this structure is finished. <code>addGroup</code>
     * and <code>addOptionInfo</code> will have no effect after call of this
     * function
     *
     * @see #addGroup(String)
     * @see #addOptionInfo(IBasicOptionGroup, String)
     */
    void finish() {
        finished = true;
    }

    private void setOptionInfo(final String name,
                               final OptionInfo info) {
        optionsHash.put(name, info);
    }

    private String getOptionDisplayableName(final String optionName) {
        return Messages.getString(name + OPTION_SUFFIX + optionName
                + DISPLAYABLE_NAME_SUFFIX);
    }

    private String getOptionDescription(final String optionName) {
        return Messages.getString(name + OPTION_SUFFIX + optionName
                + DESCRIPTION_SUFFIX);
    }

    /**
     * Private model class to store the option info
     *
     * @see #addOptionInfo(IBasicOptionGroup, String)
     * @see #getOptionInfo(String)
     */
    private class OptionInfo implements IOptionInfo {

        private final String name;
        private static final int TEXT_FIELD_LENGTH = 3;

        private static final boolean LABEL_BEFORE_TEXT_FIELD = false;

        OptionInfo(final String optionName) {
            this.name = optionName;
        }

        public String getDisplayableName() {
            return getOptionDisplayableName(name);
        }

        public String getDisplayableNameWithValue() {
            return getOptionDisplayableName(name);
        }

        public String getDescription() {
            return getOptionDescription(name);
        }

        public int getTextFieldLength() {
            return TEXT_FIELD_LENGTH;
        }

        public boolean isLabelBeforeTextField() {
            return LABEL_BEFORE_TEXT_FIELD;
        }

    }

}
