package com.ghostofpq.kulkan.entities.inventory;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.inventory.item.*;

import java.io.Serializable;

public class Equipment implements Serializable {
    private Helm helm;
    private Armor armor;
    private Weapon weapon;
    private HeldItem heldItem;
    private Necklace necklace;
    private Ring ring;

    public Equipment() {
    }

    public Helm getHelm() {
        return helm;
    }

    public void setHelm(Helm helm) {
        this.helm = helm;
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public HeldItem getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(HeldItem heldItem) {
        this.heldItem = heldItem;
    }

    public Necklace getNecklace() {
        return necklace;
    }

    public void setNecklace(Necklace necklace) {
        this.necklace = necklace;
    }

    public Ring getRing() {
        return ring;
    }

    public void setRing(Ring ring) {
        this.ring = ring;
    }

    public PrimaryCharacteristics getPrimaryCharacteristics() {
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        if (null != helm) {
            primaryCharacteristics.plus(helm.getPrimaryCharacteristics());
        }
        if (null != armor) {
            primaryCharacteristics.plus(armor.getPrimaryCharacteristics());
        }
        if (null != weapon) {
            primaryCharacteristics.plus(weapon.getPrimaryCharacteristics());
        }
        if (null != heldItem) {
            primaryCharacteristics.plus(heldItem.getPrimaryCharacteristics());
        }
        if (null != necklace) {
            primaryCharacteristics.plus(necklace.getPrimaryCharacteristics());
        }
        if (null != ring) {
            primaryCharacteristics.plus(ring.getPrimaryCharacteristics());
        }
        return primaryCharacteristics;
    }

    public SecondaryCharacteristics getSecondaryCharacteristics() {
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        if (null != helm) {
            secondaryCharacteristics.plus(helm.getSecondaryCharacteristics());
        }
        if (null != armor) {
            secondaryCharacteristics.plus(armor.getSecondaryCharacteristics());
        }
        if (null != weapon) {
            secondaryCharacteristics.plus(weapon.getSecondaryCharacteristics());
        }
        if (null != heldItem) {
            secondaryCharacteristics.plus(heldItem.getSecondaryCharacteristics());
        }
        if (null != necklace) {
            secondaryCharacteristics.plus(necklace.getSecondaryCharacteristics());
        }
        if (null != ring) {
            secondaryCharacteristics.plus(ring.getSecondaryCharacteristics());
        }
        return secondaryCharacteristics;
    }
}
