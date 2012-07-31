/*
 * MegaMek - Copyright (C) 2003, 2004 Ben Mazur (bmazur@sev.org)
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

package megamek.client.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import megamek.client.Client;
import megamek.client.ui.GBC;
import megamek.client.ui.Messages;
import megamek.common.Aero;
import megamek.common.AmmoType;
import megamek.common.BattleArmor;
import megamek.common.Dropship;
import megamek.common.Entity;
import megamek.common.FighterSquadron;
import megamek.common.Infantry;
import megamek.common.Jumpship;
import megamek.common.Mech;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.PlanetaryConditions;
import megamek.common.Protomech;
import megamek.common.SmallCraft;
import megamek.common.TechConstants;
import megamek.common.WeaponType;

/**
 * This class builds the Equipment Panel for use in MegaMek and MekHQ
 *
 * @author Dylan Myers (ralgith-erian@users.sourceforge.net)
 * @since 2012-05-20
 */
public class EquipChoicePanel extends JPanel implements Serializable {
    static final long serialVersionUID = 672299770230285567L;

    private final Entity entity;

    private int[] entityCorrespondance;

    private ArrayList<MunitionChoicePanel> m_vMunitions = new ArrayList<MunitionChoicePanel>();
    private JPanel panMunitions = new JPanel();

    private ArrayList<RapidfireMGPanel> m_vMGs = new ArrayList<RapidfireMGPanel>();
    private JPanel panRapidfireMGs = new JPanel();

    private InfantryArmorPanel panInfArmor = new InfantryArmorPanel();

    private ArrayList<MineChoicePanel> m_vMines = new ArrayList<MineChoicePanel>();
    private JPanel panMines = new JPanel();

    private ArrayList<SantaAnnaChoicePanel> m_vSantaAnna = new ArrayList<SantaAnnaChoicePanel>();
    private JPanel panSantaAnna = new JPanel();

    private BombChoicePanel m_bombs;
    private JPanel panBombs = new JPanel();

    private EquipChoicePanel m_equip;
    private JPanel panEquip = new JPanel(new GridBagLayout());

    private JLabel labAutoEject = new JLabel(
            Messages.getString("CustomMechDialog.labAutoEject"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JCheckBox chAutoEject = new JCheckBox();

    private JLabel labCondEjectAmmo = new JLabel(
            Messages.getString("CustomMechDialog.labConditional_Ejection_Ammo"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JCheckBox chCondEjectAmmo = new JCheckBox();

    private JLabel labCondEjectEngine = new JLabel(
            Messages.getString("CustomMechDialog.labConditional_Ejection_Engine"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JCheckBox chCondEjectEngine = new JCheckBox();

    private JLabel labCondEjectCTDest = new JLabel(
            Messages.getString("CustomMechDialog.labConditional_Ejection_CT_Destroyed"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JCheckBox chCondEjectCTDest = new JCheckBox();

    private JLabel labCondEjectHeadshot = new JLabel(
            Messages.getString("CustomMechDialog.labConditional_Ejection_Headshot"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JCheckBox chCondEjectHeadshot = new JCheckBox();

    private JLabel labSearchlight = new JLabel(
            Messages.getString("CustomMechDialog.labSearchlight"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JCheckBox chSearchlight = new JCheckBox();

    private JLabel labC3 = new JLabel(
            Messages.getString("CustomMechDialog.labC3"), SwingConstants.RIGHT); //$NON-NLS-1$
    private JComboBox choC3 = new JComboBox();

    ClientGUI clientgui;
    Client client;

    public EquipChoicePanel(Entity entity, ClientGUI clientgui, Client client) {
        this.entity = entity;
        this.clientgui = clientgui;
        this.client = client;

        GridBagLayout g = new GridBagLayout();
        setLayout(g);
        GridBagConstraints c = new GridBagConstraints();

        // **EQUIPMENT TAB**//
        // Auto-eject checkbox and conditional ejections.
        if (entity instanceof Mech) {
            Mech mech = (Mech) entity;

            // Ejection Seat
            boolean hasEjectSeat = true;
            // torso mounted cockpits don't have an ejection seat
            if (mech.getCockpitType() == Mech.COCKPIT_TORSO_MOUNTED) {
                hasEjectSeat = false;
            }
            if (mech.isIndustrial()) {
                hasEjectSeat = false;
                // industrials can only eject when they have an ejection seat
                for (Mounted misc : mech.getMisc()) {
                    if (misc.getType().hasFlag(MiscType.F_EJECTION_SEAT)) {
                        hasEjectSeat = true;
                    }
                }
            }
            if (hasEjectSeat) {
                add(labAutoEject, GBC.std());
                add(chAutoEject, GBC.eol());
                chAutoEject.setSelected(!mech.isAutoEject());
            }

            // Conditional Ejections
            if (clientgui.getClient().game.getOptions().booleanOption(
                    "conditional_ejection")
                    && hasEjectSeat) { //$NON-NLS-1$
                add(labCondEjectAmmo, GBC.std());
                add(chCondEjectAmmo, GBC.eol());
                chCondEjectAmmo.setSelected(mech.isCondEjectAmmo());
                add(labCondEjectEngine, GBC.std());
                add(chCondEjectEngine, GBC.eol());
                chCondEjectEngine.setSelected(mech.isCondEjectEngine());
                add(labCondEjectCTDest, GBC.std());
                add(chCondEjectCTDest, GBC.eol());
                chCondEjectCTDest.setSelected(mech.isCondEjectCTDest());
                add(labCondEjectHeadshot, GBC.std());
                add(chCondEjectHeadshot, GBC.eol());
                chCondEjectHeadshot.setSelected(mech.isCondEjectHeadshot());
            }
        }

        if (entity.hasC3() || entity.hasC3i()) {
            add(labC3, GBC.std());
            add(choC3, GBC.eol());
            refreshC3();
        }

        // Can't set up munitions on infantry.
        if (!((entity instanceof Infantry) && !((Infantry) entity)
                .hasFieldGun()) || (entity instanceof BattleArmor)) {
            setupMunitions();
            add(panMunitions,
                    GBC.eop().anchor(GridBagConstraints.CENTER));
        }

        // set up Santa Annas if using nukes
        if (((entity instanceof Dropship) || (entity instanceof Jumpship))
                && clientgui.getClient().game.getOptions().booleanOption(
                        "at2_nukes")) {
            setupSantaAnna();
            add(panSantaAnna,
                    GBC.eop().anchor(GridBagConstraints.CENTER));
        }

        if ((entity instanceof Aero)
                && !((entity instanceof FighterSquadron)
                        || (entity instanceof SmallCraft) || (entity instanceof Jumpship))) {
            setupBombs();
            add(panBombs, GBC.eop().anchor(GridBagConstraints.CENTER));
        }

        // Set up rapidfire mg
        if (clientgui.getClient().game.getOptions().booleanOption(
                "tacops_burst")) { //$NON-NLS-1$
            setupRapidfireMGs();
            add(panRapidfireMGs,
                    GBC.eop().anchor(GridBagConstraints.CENTER));
        }

        // set up infantry armor
        if ((entity instanceof Infantry) && !(entity instanceof BattleArmor)) {
            panInfArmor.initialize();
            add(panInfArmor,
                    GBC.eop().anchor(GridBagConstraints.CENTER));
        }

        // Set up searchlight
        if (clientgui.getClient().game.getPlanetaryConditions().getLight() > PlanetaryConditions.L_DUSK) {
            add(labSearchlight, GBC.std());
            add(chSearchlight, GBC.eol());
            chSearchlight.setSelected(entity.hasSpotlight());
        }

        // Set up mines
        setupMines();
        add(panMines, GBC.eop().anchor(GridBagConstraints.CENTER));
	}

    public void initialize() {
        choC3.setEnabled(false);
        chAutoEject.setEnabled(false);
        chSearchlight.setEnabled(false);
        m_bombs.setEnabled(false);
        disableMunitionEditing();
        disableMGSetting();
        disableMineSetting();
        panInfArmor.setEnabled(false);
	}

    public void applyChoices() {
        boolean autoEject = chAutoEject.isSelected();
        boolean condEjectAmmo = chCondEjectAmmo.isSelected();
        boolean condEjectEngine = chCondEjectEngine.isSelected();
        boolean condEjectCTDest = chCondEjectCTDest.isSelected();
        boolean condEjectHeadshot = chCondEjectHeadshot.isSelected();

        if (entity instanceof Mech) {
            Mech mech = (Mech) entity;
            mech.setAutoEject(!autoEject);
            mech.setCondEjectAmmo(condEjectAmmo);
            mech.setCondEjectEngine(condEjectEngine);
            mech.setCondEjectCTDest(condEjectCTDest);
            mech.setCondEjectHeadshot(condEjectHeadshot);
        }

        // update munitions selections
        for (final Object newVar2 : m_vMunitions) {
            ((MunitionChoicePanel) newVar2).applyChoice();
        }
        // update MG rapid fire settings
        for (final Object newVar1 : m_vMGs) {
            ((RapidfireMGPanel) newVar1).applyChoice();
        }
        // update mines setting
        for (final Object newVar : m_vMines) {
            ((MineChoicePanel) newVar).applyChoice();
        }
        // update Santa Anna setting
        for (final Object newVar : m_vSantaAnna) {
            ((SantaAnnaChoicePanel) newVar).applyChoice();
        }
        // update bomb setting
        if (null != m_bombs) {
            m_bombs.applyChoice();
        }
        if ((entity instanceof Infantry)
                && !(entity instanceof BattleArmor)) {
            panInfArmor.applyChoice();
        }

        // update searchlight setting
        entity.setSpotlight(chSearchlight.isSelected());
        entity.setSpotlightState(chSearchlight.isSelected());

        if (entity.hasC3() && (choC3.getSelectedIndex() > -1)) {
            Entity chosen = client.getEntity(entityCorrespondance[choC3
                    .getSelectedIndex()]);
            int entC3nodeCount = client.game.getC3SubNetworkMembers(entity)
                    .size();
            int choC3nodeCount = client.game.getC3NetworkMembers(chosen)
                    .size();
            if ((entC3nodeCount + choC3nodeCount) <= Entity.MAX_C3_NODES) {
                entity.setC3Master(chosen);
            } else {
                String message = Messages
                        .getString(
                                "CustomMechDialog.NetworkTooBig.message", new Object[] {//$NON-NLS-1$
                                entity.getShortName(),
                                        chosen.getShortName(),
                                        new Integer(entC3nodeCount),
                                        new Integer(choC3nodeCount),
                                        new Integer(Entity.MAX_C3_NODES) });
                clientgui.doAlertDialog(Messages
                        .getString("CustomMechDialog.NetworkTooBig.title"), //$NON-NLS-1$
                        message);
                refreshC3();
            }
        } else if (entity.hasC3i() && (choC3.getSelectedIndex() > -1)) {
            entity.setC3NetId(client.getEntity(entityCorrespondance[choC3
                    .getSelectedIndex()]));
        }
	}

    private void setupBombs() {
        GridBagLayout gbl = new GridBagLayout();
        panBombs.setLayout(gbl);

        m_bombs = new BombChoicePanel((Aero) entity, client.game.getOptions().booleanOption("at2_nukes"),
                client.game.getOptions().booleanOption("allow_advanced_ammo"));
        panBombs.add(m_bombs, GBC.std());
    }

    private void setupRapidfireMGs() {
        GridBagLayout gbl = new GridBagLayout();
        panRapidfireMGs.setLayout(gbl);
        for (Mounted m : entity.getWeaponList()) {
            WeaponType wtype = (WeaponType) m.getType();
            if (!wtype.hasFlag(WeaponType.F_MG)) {
                continue;
            }
            RapidfireMGPanel rmp = new RapidfireMGPanel(m);
            panRapidfireMGs.add(rmp, GBC.eol());
            m_vMGs.add(rmp);
        }
    }

    private void setupMines() {
        GridBagLayout gbl = new GridBagLayout();
        panMines.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();

        int row = 0;
        for (Mounted m : entity.getMisc()) {
            if (!m.getType().hasFlag((MiscType.F_MINE))) {
                continue;
            }

            gbc.gridy = row++;
            MineChoicePanel mcp = new MineChoicePanel(m);
            gbl.setConstraints(mcp, gbc);
            panMines.add(mcp);
            m_vMines.add(mcp);
        }
    }

    private void setupSantaAnna() {
        GridBagLayout gbl = new GridBagLayout();
        panSantaAnna.setLayout(gbl);
        for (Mounted m : entity.getAmmo()) {
            AmmoType at = (AmmoType) m.getType();
            // Santa Annas?
            if (clientgui.getClient().game.getOptions().booleanOption(
                    "at2_nukes")
                    && ((at.getAmmoType() == AmmoType.T_KILLER_WHALE) || ((at
                            .getAmmoType() == AmmoType.T_AR10) && at
                            .hasFlag(AmmoType.F_AR10_KILLER_WHALE)))) {
                SantaAnnaChoicePanel sacp = new SantaAnnaChoicePanel(m);
                panSantaAnna.add(sacp, GBC.eol());
                m_vSantaAnna.add(sacp);
            }
        }
    }

    private void setupMunitions() {
        GridBagLayout gbl = new GridBagLayout();
        panMunitions.setLayout(gbl);
        for (Mounted m : entity.getAmmo()) {
            AmmoType at = (AmmoType) m.getType();
            ArrayList<AmmoType> vTypes = new ArrayList<AmmoType>();
            Vector<AmmoType> vAllTypes = AmmoType.getMunitionsFor(at
                    .getAmmoType());
            if (vAllTypes == null) {
                continue;
            }

            // don't allow ammo switching of most things for Aeros
            // allow only MML, ATM, NARC, and LBX switching
            // TODO: need a better way to customize munitions on Aeros
            // currently this doesn't allow AR10 and tele-missile launchers
            // to switch back and forth between tele and regular missiles
            // also would be better to not have to add Santa Anna's in such
            // an idiosyncratic fashion
            if ((entity instanceof Aero)
                    && !((at.getAmmoType() == AmmoType.T_MML)
                            || (at.getAmmoType() == AmmoType.T_ATM)
                            || (at.getAmmoType() == AmmoType.T_NARC) || (at
                            .getAmmoType() == AmmoType.T_AC_LBX))) {
                continue;
            }

            for (int x = 0, n = vAllTypes.size(); x < n; x++) {
                AmmoType atCheck = vAllTypes.elementAt(x);
                boolean bTechMatch = TechConstants.isLegal(
                        entity.getTechLevel(), atCheck.getTechLevel(), true,
                        entity.isMixedTech());

                // allow all lvl2 IS units to use level 1 ammo
                // lvl1 IS units don't need to be allowed to use lvl1 ammo,
                // because there is no special lvl1 ammo, therefore it doesn't
                // need to show up in this display.
                if (!bTechMatch
                        && (entity.getTechLevel() == TechConstants.T_IS_TW_NON_BOX)
                        && (atCheck.getTechLevel() == TechConstants.T_INTRO_BOXSET)) {
                    bTechMatch = true;
                }

                // if is_eq_limits is unchecked allow l1 guys to use l2 stuff
                if (!clientgui.getClient().game.getOptions().booleanOption(
                        "is_eq_limits") //$NON-NLS-1$
                        && (entity.getTechLevel() == TechConstants.T_INTRO_BOXSET)
                        && (atCheck.getTechLevel() == TechConstants.T_IS_TW_NON_BOX)) {
                    bTechMatch = true;
                }

                // Possibly allow advanced/experimental ammos, possibly not.
                if (clientgui.getClient().game.getOptions().booleanOption(
                        "allow_advanced_ammo")) {
                    if (!clientgui.getClient().game.getOptions().booleanOption(
                            "is_eq_limits")) {
                        if (((entity.getTechLevel() == TechConstants.T_CLAN_TW) || (entity
                                .getTechLevel() == TechConstants.T_CLAN_ADVANCED))
                                && ((atCheck.getTechLevel() == TechConstants.T_CLAN_ADVANCED)
                                        || (atCheck.getTechLevel() == TechConstants.T_CLAN_EXPERIMENTAL) || (atCheck
                                        .getTechLevel() == TechConstants.T_CLAN_UNOFFICIAL))) {
                            bTechMatch = true;
                        }
                        if (((entity.getTechLevel() == TechConstants.T_INTRO_BOXSET)
                                || (entity.getTechLevel() == TechConstants.T_IS_TW_NON_BOX) || (entity
                                .getTechLevel() == TechConstants.T_IS_ADVANCED))
                                && ((atCheck.getTechLevel() == TechConstants.T_IS_ADVANCED)
                                        || (atCheck.getTechLevel() == TechConstants.T_IS_EXPERIMENTAL) || (atCheck
                                        .getTechLevel() == TechConstants.T_IS_UNOFFICIAL))) {
                            bTechMatch = true;
                        }
                    }
                } else if ((atCheck.getTechLevel() == TechConstants.T_IS_ADVANCED)
                        || (atCheck.getTechLevel() == TechConstants.T_CLAN_ADVANCED)) {
                    bTechMatch = false;
                }

                    // allow mixed Tech Mechs to use both IS and Clan ammo of any
                    // level (since mixed tech is always level 3)
                    if (entity.isMixedTech()) {
                        bTechMatch = true;
                    }

                    // If clan_ignore_eq_limits is unchecked,
                    // do NOT allow Clans to use IS-only ammo.
                    // N.B. play bit-shifting games to allow "incendiary"
                    // to be combined to other munition types.
                    long muniType = atCheck.getMunitionType();
                    muniType &= ~AmmoType.M_INCENDIARY_LRM;
                    if (!clientgui.getClient().game.getOptions().booleanOption(
                            "clan_ignore_eq_limits") //$NON-NLS-1$
                            && entity.isClan()
                            && ((muniType == AmmoType.M_SEMIGUIDED)
                                    || (muniType == AmmoType.M_SWARM_I)
                                    || (muniType == AmmoType.M_FLARE)
                                    || (muniType == AmmoType.M_FRAGMENTATION)
                                    || (muniType == AmmoType.M_THUNDER_AUGMENTED)
                                    || (muniType == AmmoType.M_THUNDER_INFERNO)
                                    || (muniType == AmmoType.M_THUNDER_VIBRABOMB)
                                    || (muniType == AmmoType.M_THUNDER_ACTIVE)
                                    || (muniType == AmmoType.M_INFERNO_IV)
                                    || (muniType == AmmoType.M_VIBRABOMB_IV)
                                    || (muniType == AmmoType.M_LISTEN_KILL)
                                    || (muniType == AmmoType.M_ANTI_TSM) || (muniType == AmmoType.M_SMOKE_WARHEAD))) {
                        bTechMatch = false;
                    }

                    if (!clientgui.getClient().game.getOptions().booleanOption(
                            "minefields") && //$NON-NLS-1$
                            AmmoType.canDeliverMinefield(atCheck)) {
                        continue;
                    }

                    // Only Protos can use Proto-specific ammo
                    if (atCheck.hasFlag(AmmoType.F_PROTOMECH)
                            && !(entity instanceof Protomech)) {
                        continue;
                    }

                    // When dealing with machine guns, Protos can only
                    // use proto-specific machine gun ammo
                    if ((entity instanceof Protomech)
                            && atCheck.hasFlag(AmmoType.F_MG)
                            && !atCheck.hasFlag(AmmoType.F_PROTOMECH)) {
                        continue;
                    }

                    // Battle Armor ammo can't be selected at all.
                    // All other ammo types need to match on rack size and tech.
                    if (bTechMatch
                            && (atCheck.getRackSize() == at.getRackSize())
                            && (atCheck.hasFlag(AmmoType.F_BATTLEARMOR) == at
                                    .hasFlag(AmmoType.F_BATTLEARMOR))
                            && (atCheck.hasFlag(AmmoType.F_ENCUMBERING) == at
                                    .hasFlag(AmmoType.F_ENCUMBERING))
                            && (atCheck.getTonnage(entity) == at.getTonnage(entity))) {
                        vTypes.add(atCheck);
                    }
                }
                if ((vTypes.size() < 2)
                        && !client.game.getOptions().booleanOption(
                                "lobby_ammo_dump")
                        && !client.game.getOptions()
                                .booleanOption("tacops_hotload")) { //$NON-NLS-1$
                    continue;
                }
                // Protomechs need special choice panels.
                MunitionChoicePanel mcp;
                if (entity instanceof Protomech) {
                    mcp = new ProtomechMunitionChoicePanel(m, vTypes);
                } else {
                    mcp = new MunitionChoicePanel(m, vTypes);
                }
                panMunitions.add(mcp, GBC.eol());
                m_vMunitions.add(mcp);
            }
        }

        class MineChoicePanel extends JPanel {
            /**
             *
             */
            private static final long serialVersionUID = -1868675102440527538L;

            private JComboBox m_choice;

            private Mounted m_mounted;

            MineChoicePanel(Mounted m) {
                m_mounted = m;
                m_choice = new JComboBox();
                m_choice.addItem(Messages
                        .getString("CustomMechDialog.Conventional")); //$NON-NLS-1$
                m_choice.addItem(Messages.getString("CustomMechDialog.Vibrabomb")); //$NON-NLS-1$
                // m_choice.add("Messages.getString("CustomMechDialog.Command-detonated"));
                // //$NON-NLS-1$
                int loc;
                loc = m.getLocation();
                String sDesc = '(' + entity.getLocationAbbr(loc) + ')';
                JLabel lLoc = new JLabel(sDesc);
                GridBagLayout gbl = new GridBagLayout();
                setLayout(gbl);
                add(lLoc, GBC.std());
                m_choice.setSelectedIndex(m.getMineType());
                add(m_choice, GBC.eol());
            }

            public void applyChoice() {
                m_mounted.setMineType(m_choice.getSelectedIndex());
            }

            @Override
            public void setEnabled(boolean enabled) {
                m_choice.setEnabled(enabled);
            }
        }

        class MunitionChoicePanel extends JPanel {
            /**
             *
             */
            private static final long serialVersionUID = 3401106035583965326L;

            private ArrayList<AmmoType> m_vTypes;

            private JComboBox m_choice;

            private Mounted m_mounted;

            JLabel labDump = new JLabel(
                    Messages.getString("CustomMechDialog.labDump")); //$NON-NLS-1$

            JCheckBox chDump = new JCheckBox();

            JLabel labHotLoad = new JLabel(
                    Messages.getString("CustomMechDialog.switchToHotLoading")); //$NON-NLS-1$

            JCheckBox chHotLoad = new JCheckBox();

            MunitionChoicePanel(Mounted m, ArrayList<AmmoType> vTypes) {
                m_vTypes = vTypes;
                m_mounted = m;
                AmmoType curType = (AmmoType) m.getType();
                m_choice = new JComboBox();
                Iterator<AmmoType> e = m_vTypes.iterator();
                for (int x = 0; e.hasNext(); x++) {
                    AmmoType at = e.next();
                    m_choice.addItem(at.getName());
                    if (at.getInternalName() == curType.getInternalName()) {
                        m_choice.setSelectedIndex(x);
                    }
                }
                int loc;
                if (m.getLocation() == Entity.LOC_NONE) {
                    // oneshot weapons don't have a location of their own
                    Mounted linkedBy = m.getLinkedBy();
                    loc = linkedBy.getLocation();
                } else {
                    loc = m.getLocation();
                }
                String sDesc = '(' + entity.getLocationAbbr(loc) + ')';
                JLabel lLoc = new JLabel(sDesc);
                GridBagLayout g = new GridBagLayout();
                setLayout(g);
                add(lLoc, GBC.std());
                add(m_choice, GBC.eol());
                if (clientgui.getClient().game.getOptions().booleanOption(
                        "lobby_ammo_dump")) { //$NON-NLS-1$
                    add(labDump, GBC.std());
                    add(chDump, GBC.eol());
                    if (clientgui.getClient().game.getOptions().booleanOption(
                            "tacops_hotload")
                            && curType.hasFlag(AmmoType.F_HOTLOAD)) {
                        add(labHotLoad, GBC.std());
                        add(chHotLoad, GBC.eol());
                    }
                } else if (clientgui.getClient().game.getOptions().booleanOption(
                        "tacops_hotload")
                        && curType.hasFlag(AmmoType.F_HOTLOAD)) {
                    add(labHotLoad, GBC.std());
                    add(chHotLoad, GBC.eol());
                }
            }

            public void applyChoice() {
                int n = m_choice.getSelectedIndex();
                AmmoType at = m_vTypes.get(n);
                m_mounted.changeAmmoType(at);
                if (chDump.isSelected()) {
                    m_mounted.setShotsLeft(0);
                }
                if (clientgui.getClient().game.getOptions().booleanOption(
                        "tacops_hotload")) {
                    if (chHotLoad.isSelected() != m_mounted.isHotLoaded()) {
                        m_mounted.setHotLoad(chHotLoad.isSelected());
                    }
                }
            }

            @Override
            public void setEnabled(boolean enabled) {
                m_choice.setEnabled(enabled);
            }

            /**
             * Get the number of shots in the mount.
             *
             * @return the <code>int</code> number of shots in the mount.
             */
            int getShotsLeft() {
                return m_mounted.getBaseShotsLeft();
            }

            /**
             * Set the number of shots in the mount.
             *
             * @param shots
             *            the <code>int</code> number of shots for the mount.
             */
            void setShotsLeft(int shots) {
                m_mounted.setShotsLeft(shots);
            }
        }

        // a choice panel for determining number of santa anna warheads
        class SantaAnnaChoicePanel extends JPanel {
            /**
             *
             */
            private static final long serialVersionUID = -1645895479085898410L;

            private JComboBox m_choice;

            private Mounted m_mounted;

            public SantaAnnaChoicePanel(Mounted m) {
                m_mounted = m;
                m_choice = new JComboBox();
                for (int i = 0; i <= m_mounted.getBaseShotsLeft(); i++) {
                    m_choice.addItem(Integer.toString(i));
                }
                int loc;
                loc = m.getLocation();
                String sDesc = "Nuclear warheads for " + m_mounted.getName() + " (" + entity.getLocationAbbr(loc) + "):"; //$NON-NLS-1$ //$NON-NLS-2$
                JLabel lLoc = new JLabel(sDesc);
                GridBagLayout g = new GridBagLayout();
                setLayout(g);
                add(lLoc, GBC.std());
                m_choice.setSelectedIndex(m.getNSantaAnna());
                add(m_choice, GBC.eol());
            }

            public void applyChoice() {
                // this is a hack. I can't immediately apply the choice, because
                // that would split this ammo bin in two and then the player could
                // never
                // get back to it. So I keep track of the Santa Anna allocation
                // on the mounted and then apply it before deployment
                m_mounted.setNSantaAnna(m_choice.getSelectedIndex());
            }

            @Override
            public void setEnabled(boolean enabled) {
                m_choice.setEnabled(enabled);
            }
        }

        /**
         * When a Protomech selects ammo, you need to adjust the shots on the unit
         * for the weight of the selected munition.
         */
        class ProtomechMunitionChoicePanel extends MunitionChoicePanel {
            /**
             *
             */
            private static final long serialVersionUID = -8170286698673268120L;

            private final float m_origShotsLeft;

            private final AmmoType m_origAmmo;

            ProtomechMunitionChoicePanel(Mounted m, ArrayList<AmmoType> vTypes) {
                super(m, vTypes);
                m_origAmmo = (AmmoType) m.getType();
                m_origShotsLeft = m.getBaseShotsLeft();
            }

            /**
             * All ammo must be applied in ratios to the starting load.
             */
            @Override
            public void applyChoice() {
                super.applyChoice();

                // Calculate the number of shots for the new ammo.
                // N.B. Some special ammos are twice as heavy as normal
                // so they have half the number of shots (rounded down).
                setShotsLeft(Math.round((getShotsLeft() * m_origShotsLeft)
                        / m_origAmmo.getShots()));
                if (chDump.isSelected()) {
                    setShotsLeft(0);
                }
            }
        }

        class RapidfireMGPanel extends JPanel {
            /**
             *
             */
            private static final long serialVersionUID = 5261919826318225201L;

            private Mounted m_mounted;

            JCheckBox chRapid = new JCheckBox();

            RapidfireMGPanel(Mounted m) {
                m_mounted = m;
                int loc = m.getLocation();
                String sDesc = Messages
                        .getString(
                                "CustomMechDialog.switchToRapidFire", new Object[] { entity.getLocationAbbr(loc) }); //$NON-NLS-1$
                JLabel labRapid = new JLabel(sDesc);
                GridBagLayout g = new GridBagLayout();
                setLayout(g);
                add(labRapid, GBC.std().anchor(GridBagConstraints.EAST));
                chRapid.setSelected(m.isRapidfire());
                add(chRapid, GBC.eol());
            }

            public void applyChoice() {
                boolean b = chRapid.isSelected();
                m_mounted.setRapidfire(b);
            }

            @Override
            public void setEnabled(boolean enabled) {
                chRapid.setEnabled(enabled);
            }
        }

        class InfantryArmorPanel extends JPanel {
            /**
             *
             */
            private static final long serialVersionUID = -909995917737642853L;

            private Infantry inf;
            JLabel labArmor = new JLabel(
                    Messages.getString("CustomMechDialog.labInfantryArmor"));
            JLabel labDivisor = new JLabel(
                    Messages.getString("CustomMechDialog.labDamageDivisor"));
            JLabel labEncumber = new JLabel(
                    Messages.getString("CustomMechDialog.labEncumber"));
            JLabel labSpaceSuit = new JLabel(
                    Messages.getString("CustomMechDialog.labSpaceSuit"));
            JLabel labDEST = new JLabel(
                    Messages.getString("CustomMechDialog.labDEST"));
            JLabel labSneakCamo = new JLabel(
                    Messages.getString("CustomMechDialog.labSneakCamo"));
            JLabel labSneakIR = new JLabel(
                    Messages.getString("CustomMechDialog.labSneakIR"));
            JLabel labSneakECM = new JLabel(
                    Messages.getString("CustomMechDialog.labSneakECM"));
            private JTextField fldDivisor = new JTextField(3);
            JCheckBox chEncumber = new JCheckBox();
            JCheckBox chSpaceSuit = new JCheckBox();
            JCheckBox chDEST = new JCheckBox();
            JCheckBox chSneakCamo = new JCheckBox();
            JCheckBox chSneakIR = new JCheckBox();
            JCheckBox chSneakECM = new JCheckBox();

            InfantryArmorPanel() {
                GridBagLayout g = new GridBagLayout();
                setLayout(g);
                add(labArmor, GBC.eol());
                add(labDivisor, GBC.std());
                add(fldDivisor, GBC.eol());
                add(labEncumber, GBC.std());
                add(chEncumber, GBC.eol());
                add(labSpaceSuit, GBC.std());
                add(chSpaceSuit, GBC.eol());
                add(labDEST, GBC.std());
                add(chDEST, GBC.eol());
                add(labSneakCamo, GBC.std());
                add(chSneakCamo, GBC.eol());
                add(labSneakIR, GBC.std());
                add(chSneakIR, GBC.eol());
                add(labSneakECM, GBC.std());
                add(chSneakECM, GBC.eol());
            }

            public void initialize() {
                inf = (Infantry) entity;
                fldDivisor.setText(Double.toString(inf.getDamageDivisor()));
                chEncumber.setSelected(inf.isArmorEncumbering());
                chSpaceSuit.setSelected(inf.hasSpaceSuit());
                chDEST.setSelected(inf.hasDEST());
                chSneakCamo.setSelected(inf.hasSneakCamo());
                chSneakIR.setSelected(inf.hasSneakIR());
                chSneakECM.setSelected(inf.hasSneakECM());
                if (chDEST.isSelected()) {
                    chSneakCamo.setEnabled(false);
                    chSneakIR.setEnabled(false);
                    chSneakECM.setEnabled(false);
                }
                chDEST.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent event) {
                        if (event.getStateChange() == ItemEvent.SELECTED) {
                            chSneakCamo.setSelected(false);
                            chSneakCamo.setEnabled(false);
                            chSneakIR.setSelected(false);
                            chSneakIR.setEnabled(false);
                            chSneakECM.setSelected(false);
                            chSneakECM.setEnabled(false);
                        } else if (event.getStateChange() == ItemEvent.DESELECTED) {
                            chSneakCamo.setEnabled(true);
                            chSneakIR.setEnabled(true);
                            chSneakECM.setEnabled(true);
                        }
                    }
                });
            }

            public void applyChoice() {
                inf.setDamageDivisor(Double.valueOf(fldDivisor.getText()));
                inf.setArmorEncumbering(chEncumber.isSelected());
                inf.setSpaceSuit(chSpaceSuit.isSelected());
                inf.setDEST(chDEST.isSelected());
                inf.setSneakCamo(chSneakCamo.isSelected());
                inf.setSneakIR(chSneakIR.isSelected());
                inf.setSneakECM(chSneakECM.isSelected());

            }

            @Override
            public void setEnabled(boolean enabled) {
                fldDivisor.setEnabled(enabled);
                chEncumber.setEnabled(enabled);
                chSpaceSuit.setEnabled(enabled);
                chDEST.setEnabled(enabled);
                chSneakCamo.setEnabled(enabled);
                chSneakIR.setEnabled(enabled);
                chSneakECM.setEnabled(enabled);
            }
        }

        private void disableMunitionEditing() {
            for (int i = 0; i < m_vMunitions.size(); i++) {
                m_vMunitions.get(i).setEnabled(false);
            }
        }

        private void disableMGSetting() {
            for (int i = 0; i < m_vMGs.size(); i++) {
                m_vMGs.get(i).setEnabled(false);
            }
        }

        private void disableMineSetting() {
            for (int i = 0; i < m_vMines.size(); i++) {
                m_vMines.get(i).setEnabled(false);
            }
        }

        private void refreshC3() {
            choC3.removeAllItems();
            int listIndex = 0;
            entityCorrespondance = new int[client.game.getNoOfEntities() + 2];

            if (entity.hasC3i()) {
                choC3.addItem(Messages
                        .getString("CustomMechDialog.CreateNewNetwork")); //$NON-NLS-1$
                if (entity.getC3Master() == null) {
                    choC3.setSelectedIndex(listIndex);
                }
                entityCorrespondance[listIndex++] = entity.getId();
            } else if (entity.hasC3MM()) {
                int mNodes = entity.calculateFreeC3MNodes();
                int sNodes = entity.calculateFreeC3Nodes();

                choC3.addItem(Messages
                        .getString(
                                "CustomMechDialog.setCompanyMaster", new Object[] { new Integer(mNodes), new Integer(sNodes) })); //$NON-NLS-1$

                if (entity.C3MasterIs(entity)) {
                    choC3.setSelectedIndex(listIndex);
                }
                entityCorrespondance[listIndex++] = entity.getId();

                choC3.addItem(Messages
                        .getString(
                                "CustomMechDialog.setIndependentMaster", new Object[] { new Integer(sNodes) })); //$NON-NLS-1$
                if (entity.getC3Master() == null) {
                    choC3.setSelectedIndex(listIndex);
                }
                entityCorrespondance[listIndex++] = -1;

            } else if (entity.hasC3M()) {
                int nodes = entity.calculateFreeC3Nodes();

                choC3.addItem(Messages
                        .getString(
                                "CustomMechDialog.setCompanyMaster1", new Object[] { new Integer(nodes) })); //$NON-NLS-1$
                if (entity.C3MasterIs(entity)) {
                    choC3.setSelectedIndex(listIndex);
                }
                entityCorrespondance[listIndex++] = entity.getId();

                choC3.addItem(Messages
                        .getString(
                                "CustomMechDialog.setIndependentMaster", new Object[] { new Integer(nodes) })); //$NON-NLS-1$
                if (entity.getC3Master() == null) {
                    choC3.setSelectedIndex(listIndex);
                }
                entityCorrespondance[listIndex++] = -1;

            }
            for (Enumeration<Entity> i = client.getEntities(); i.hasMoreElements();) {
                final Entity e = i.nextElement();
                // ignore enemies or self
                if (entity.isEnemyOf(e) || entity.equals(e)) {
                    continue;
                }
                // c3i only links with c3i
                if (entity.hasC3i() != e.hasC3i()) {
                    continue;
                }
                // maximum depth of a c3 network is 2 levels.
                Entity eCompanyMaster = e.getC3Master();
                if ((eCompanyMaster != null)
                        && (eCompanyMaster.getC3Master() != eCompanyMaster)) {
                    continue;
                }
                int nodes = e.calculateFreeC3Nodes();
                if (e.hasC3MM() && entity.hasC3M() && e.C3MasterIs(e)) {
                    nodes = e.calculateFreeC3MNodes();
                }
                if (entity.C3MasterIs(e) && !entity.equals(e)) {
                    nodes++;
                }
                if (entity.hasC3i()
                        && (entity.onSameC3NetworkAs(e) || entity.equals(e))) {
                    nodes++;
                }
                if (nodes == 0) {
                    continue;
                }
                if (e.hasC3i()) {
                    if (entity.onSameC3NetworkAs(e)) {
                        choC3.addItem(Messages
                                .getString(
                                        "CustomMechDialog.join1", new Object[] { e.getDisplayName(), e.getC3NetId(), new Integer(nodes - 1) })); //$NON-NLS-1$
                        choC3.setSelectedIndex(listIndex);
                    } else {
                        choC3.addItem(Messages
                                .getString(
                                        "CustomMechDialog.join2", new Object[] { e.getDisplayName(), e.getC3NetId(), new Integer(nodes) })); //$NON-NLS-1$
                    }
                    entityCorrespondance[listIndex++] = e.getId();
                } else if (e.C3MasterIs(e) && e.hasC3MM()) {
                    // Company masters with 2 computers can have
                    // *both* sub-masters AND slave units.
                    choC3.addItem(Messages
                            .getString(
                                    "CustomMechDialog.connect2", new Object[] { e.getDisplayName(), e.getC3NetId(), new Integer(nodes) })); //$NON-NLS-1$
                    entityCorrespondance[listIndex] = e.getId();
                    if (entity.C3MasterIs(e)) {
                        choC3.setSelectedIndex(listIndex);
                    }
                    listIndex++;
                } else if (e.C3MasterIs(e) != entity.hasC3M()) {
                    // If we're a slave-unit, we can only connect to sub-masters,
                    // not main masters likewise, if we're a master unit, we can
                    // only connect to main master units, not sub-masters.
                } else if (entity.C3MasterIs(e)) {
                    choC3.addItem(Messages
                            .getString(
                                    "CustomMechDialog.connect1", new Object[] { e.getDisplayName(), e.getC3NetId(), new Integer(nodes - 1) })); //$NON-NLS-1$
                    choC3.setSelectedIndex(listIndex);
                    entityCorrespondance[listIndex++] = e.getId();
                } else {
                    choC3.addItem(Messages
                            .getString(
                                    "CustomMechDialog.connect2", new Object[] { e.getDisplayName(), e.getC3NetId(), new Integer(nodes) })); //$NON-NLS-1$
                    entityCorrespondance[listIndex++] = e.getId();
                }
            }
        }
}