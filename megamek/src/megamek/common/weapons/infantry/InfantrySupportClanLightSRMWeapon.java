/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
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
/*
 * Created on Sep 7, 2005
 *
 */
package megamek.common.weapons.infantry;

import megamek.common.AmmoType;
import megamek.common.TechConstants;

/**
 * @author Sebastian Brocks
 */
public class InfantrySupportClanLightSRMWeapon extends InfantryWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -5311681183178942222L;

    public InfantrySupportClanLightSRMWeapon() {
        super();

        name = "SRM Launcher (Light)[Clan]";
        setInternalName(name);
        addLookupName("CLInfantrySRMLight");
        addLookupName("CLInfantrySRM");
        addLookupName("Clan Light SRM Launcher");
        ammoType = AmmoType.T_NA;
        cost = 1500;
        bv = 2.91;
        flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_MISSILE).or(F_INF_SUPPORT);
        infantryDamage = 0.57;
        infantryRange = 2;
        introDate = 2807;
        techLevel.put(2807, TechConstants.T_CLAN_TW);
        availRating = new int[] { RATING_X,RATING_C ,RATING_D ,RATING_C};
        techRating = RATING_C;
        rulesRefs =" 273, TM";
    }
}