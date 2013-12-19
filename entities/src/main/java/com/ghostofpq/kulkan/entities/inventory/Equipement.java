package com.ghostofpq.kulkan.entities.inventory;

import com.ghostofpq.kulkan.entities.inventory.item.*;

import java.io.Serializable;

public class Equipement implements Serializable {
    private Helm helm;
    private Armor armor;
    private Weapon weapon;
    private HeldItem heldItem;
    private Necklace necklace;
    private Ring ring;

    public Equipement() {
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
}
