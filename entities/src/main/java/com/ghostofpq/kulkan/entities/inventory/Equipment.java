package com.ghostofpq.kulkan.entities.inventory;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;

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

    public Range getRange() {
        if (weapon != null) {
            return weapon.getRange();
        } else {
            return new Range(RangeType.CROSS, 0, 1);
        }
    }

    public Characteristics getCharacteristics() {
        Characteristics characteristics = new Characteristics();
        if (null != helm) {
            characteristics.plus(helm.getCharacteristics());
        }
        if (null != armor) {
            characteristics.plus(armor.getCharacteristics());
        }
        if (null != weapon) {
            characteristics.plus(weapon.getCharacteristics());
        }
        if (null != heldItem) {
            characteristics.plus(heldItem.getCharacteristics());
        }
        if (null != necklace) {
            characteristics.plus(necklace.getCharacteristics());
        }
        if (null != ring) {
            characteristics.plus(ring.getCharacteristics());
        }
        return characteristics;
    }
}
