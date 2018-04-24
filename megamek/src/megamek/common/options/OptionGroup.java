/*
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OptionGroup implements IBasicOptionGroup, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6445683666789832313L;

    private final Vector<String> optionNames = new Vector<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final String name;
    private String key;

    /**
     * Creates new OptionGroup
     * 
     * @param name group name
     * @param key optional key
     */
    OptionGroup(final String name,
                final String key) {
        this.name = name;
        lock.writeLock().lock();
        try {
            this.key = key;
        } finally {
            lock.writeLock().lock();
        }
    }

    /**
     * Creates new OptionGroup with empty key
     * 
     * @param name option name
     */
    OptionGroup(final String name) {
        this(name, ""); //$NON-NLS-1$
    }

    public String getName() {
        return name;
    }

    public void setKey(final String key) {
        lock.writeLock().lock();
        try {
            this.key = key;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String getKey() {
        lock.readLock().lock();
        try {
            return key;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Enumeration<String> getOptionNames() {
        lock.readLock().lock();
        try {
            return optionNames.elements();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Adds new option name to this group. The option names are unique, so if
     * there is already an option <code>optionName</code> this function does
     * nothing.
     * 
     * @param optionName new option name
     */
    void addOptionName(final String optionName) {
        // This check is a performance penalty, but we don't
        // allow duplicate option names
        lock.writeLock().lock();
        try {
            if (!optionNames.contains(optionName)) {
                optionNames.addElement(optionName);
            }
        } finally {
            lock.writeLock().lock();
        }
    }

}
