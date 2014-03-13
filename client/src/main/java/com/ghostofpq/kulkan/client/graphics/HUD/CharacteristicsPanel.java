package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.characteristics.Characteristics;

public class CharacteristicsPanel extends HUDElement {

    // CHARACTERISTICS
    private HUDTexturedElement characteristicsPanel;
    private KeyValueRender maxHealthPointRender;
    private KeyValueRender maxManaPointRender;
    private KeyValueRender attackDamageRender;
    private KeyValueRender magicalDamageRender;
    private KeyValueRender armorRender;
    private KeyValueRender magicResistRender;
    private KeyValueRender armorPenetrationRender;
    private KeyValueRender magicPenetrationRender;
    private KeyValueRender movementRender;
    private KeyValueRender speedRender;
    private KeyValueRender healthRegenerationRender;
    private KeyValueRender manaRegenerationRender;
    private KeyValueRender escapeRender;
    private KeyValueRender criticalStrikeRender;
    private KeyValueRender precisionRender;
    private KeyValueRender resilienceRender;

    private Characteristics characteristics;

    public CharacteristicsPanel(int posX, int posY, int width, int height, Characteristics characteristics) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.characteristics = characteristics;
        init();
    }

    public void init() {
        int characteristicsPanelPosX = posX;
        int characteristicsPanelPosY = posY;
        int characteristicsPanelWidth = width;
        int characteristicsPanelHeight = height;
        characteristicsPanel = new HUDTexturedElement(characteristicsPanelPosX, characteristicsPanelPosY, characteristicsPanelWidth, characteristicsPanelHeight, TextureKey.MANAGE_CHAR_MENU, TextureKey.MANAGE_CHAR_MENU);

        int valueHeight = characteristicsPanelHeight / 10;
        int valuePadding = (characteristicsPanelHeight - (8 * valueHeight)) / 13;
        int valueWidth = (characteristicsPanelWidth / 2) - 4 * valuePadding;

        int hpRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int hpRenderPosY = characteristicsPanelPosY + valuePadding;
        maxHealthPointRender = new KeyValueRender(hpRenderPosX, hpRenderPosY, valueWidth, valueHeight, "HP", String.valueOf(characteristics.getMaxHealthPoint()), 5);
        maxHealthPointRender.getValue().setAlignment(TextAlignment.RIGHT);

        int mpRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int mpRenderPosY = hpRenderPosY;
        maxManaPointRender = new KeyValueRender(mpRenderPosX, mpRenderPosY, valueWidth, valueHeight, "MP", String.valueOf(characteristics.getMaxManaPoint()), 5);
        maxManaPointRender.getValue().setAlignment(TextAlignment.RIGHT);

        int attackDamageRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int attackDamageRenderPosY = hpRenderPosY + valueHeight + 2 * valuePadding;
        attackDamageRender = new KeyValueRender(attackDamageRenderPosX, attackDamageRenderPosY, valueWidth, valueHeight, "Attack", String.valueOf(characteristics.getAttackDamage()), 5);
        attackDamageRender.getValue().setAlignment(TextAlignment.RIGHT);

        int armorRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int armorRenderPosY = attackDamageRenderPosY;
        armorRender = new KeyValueRender(armorRenderPosX, armorRenderPosY, valueWidth, valueHeight, "Armor", String.valueOf(characteristics.getArmor()), 5);
        armorRender.getValue().setAlignment(TextAlignment.RIGHT);

        int magicalDamageRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int magicalDamageRenderPosY = attackDamageRenderPosY + valueHeight + valuePadding;
        magicalDamageRender = new KeyValueRender(magicalDamageRenderPosX, magicalDamageRenderPosY, valueWidth, valueHeight, "Mag. Attack", String.valueOf(characteristics.getMagicalDamage()), 5);
        magicalDamageRender.getValue().setAlignment(TextAlignment.RIGHT);

        int magicResistRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int magicResistRenderPosY = magicalDamageRenderPosY;
        magicResistRender = new KeyValueRender(magicResistRenderPosX, magicResistRenderPosY, valueWidth, valueHeight, "Mag. Armor", String.valueOf(characteristics.getMagicResist()), 5);
        magicResistRender.getValue().setAlignment(TextAlignment.RIGHT);

        int armorPenetrationRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int armorPenetrationRenderPosY = magicalDamageRenderPosY + valueHeight + valuePadding;
        armorPenetrationRender = new KeyValueRender(armorPenetrationRenderPosX, armorPenetrationRenderPosY, valueWidth, valueHeight, "Arm. Pen.", String.valueOf(characteristics.getAttackDamage()), 5);
        armorPenetrationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int magicPenetrationRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int magicPenetrationRenderPosY = armorPenetrationRenderPosY;
        magicPenetrationRender = new KeyValueRender(magicPenetrationRenderPosX, magicPenetrationRenderPosY, valueWidth, valueHeight, "Mag. Pen.", String.valueOf(characteristics.getMagicPenetration()), 5);
        magicPenetrationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int movementRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int movementRenderPosY = armorPenetrationRenderPosY + valueHeight + 2 * valuePadding;
        movementRender = new KeyValueRender(movementRenderPosX, movementRenderPosY, valueWidth, valueHeight, "Movement", String.valueOf(characteristics.getMovement()), 5);
        movementRender.getValue().setAlignment(TextAlignment.RIGHT);

        int speedRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int speedRenderPosY = movementRenderPosY;
        speedRender = new KeyValueRender(speedRenderPosX, speedRenderPosY, valueWidth, valueHeight, "Speed", String.valueOf(characteristics.getSpeed()), 5);
        speedRender.getValue().setAlignment(TextAlignment.RIGHT);


        int healthRegenerationRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int healthRegenerationRenderPosY = movementRenderPosY + valueHeight + 2 * valuePadding;
        healthRegenerationRender = new KeyValueRender(healthRegenerationRenderPosX, healthRegenerationRenderPosY, valueWidth, valueHeight, "HP Regen.", String.valueOf(characteristics.getHealthRegeneration()), 5);
        healthRegenerationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int manaRegenerationRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int manaRegenerationRenderPosY = healthRegenerationRenderPosY;
        manaRegenerationRender = new KeyValueRender(manaRegenerationRenderPosX, manaRegenerationRenderPosY, valueWidth, valueHeight, "MP Regen.", String.valueOf(characteristics.getManaRegeneration()), 5);
        manaRegenerationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int escapeRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int escapeRenderPosY = healthRegenerationRenderPosY + valueHeight + 2 * valuePadding;
        escapeRender = new KeyValueRender(escapeRenderPosX, escapeRenderPosY, valueWidth, valueHeight, "Escape", String.valueOf(characteristics.getEscape()), 5);
        escapeRender.getValue().setAlignment(TextAlignment.RIGHT);

        int criticalStrikeRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int criticalStrikeRenderPosY = escapeRenderPosY;
        criticalStrikeRender = new KeyValueRender(criticalStrikeRenderPosX, criticalStrikeRenderPosY, valueWidth, valueHeight, "Crit. Chance", String.valueOf(characteristics.getCriticalStrike()), 5);
        criticalStrikeRender.getValue().setAlignment(TextAlignment.RIGHT);

        int precisionRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int precisionRenderPosY = escapeRenderPosY + valueHeight + valuePadding;
        precisionRender = new KeyValueRender(precisionRenderPosX, precisionRenderPosY, valueWidth, valueHeight, "Precision", String.valueOf(characteristics.getPrecision()), 5);
        precisionRender.getValue().setAlignment(TextAlignment.RIGHT);

        int resilienceRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int resilienceRenderPosY = precisionRenderPosY;
        resilienceRender = new KeyValueRender(resilienceRenderPosX, resilienceRenderPosY, valueWidth, valueHeight, "Resilience", String.valueOf(characteristics.getResilience()), 5);
        resilienceRender.getValue().setAlignment(TextAlignment.RIGHT);

        maxHealthPointRender.setFont("arial_16");
        maxManaPointRender.setFont("arial_16");
        attackDamageRender.setFont("arial_16");
        armorRender.setFont("arial_16");
        magicalDamageRender.setFont("arial_16");
        magicResistRender.setFont("arial_16");
        armorPenetrationRender.setFont("arial_16");
        magicPenetrationRender.setFont("arial_16");
        movementRender.setFont("arial_16");
        speedRender.setFont("arial_16");
        healthRegenerationRender.setFont("arial_16");
        manaRegenerationRender.setFont("arial_16");
        escapeRender.setFont("arial_16");
        criticalStrikeRender.setFont("arial_16");
        precisionRender.setFont("arial_16");
        resilienceRender.setFont("arial_16");
    }

    @Override
    public void draw() {
        characteristicsPanel.draw();
        maxHealthPointRender.draw();
        maxManaPointRender.draw();
        attackDamageRender.draw();
        armorRender.draw();
        magicalDamageRender.draw();
        magicResistRender.draw();
        armorPenetrationRender.draw();
        magicPenetrationRender.draw();
        movementRender.draw();
        speedRender.draw();
        healthRegenerationRender.draw();
        manaRegenerationRender.draw();
        escapeRender.draw();
        criticalStrikeRender.draw();
        precisionRender.draw();
        resilienceRender.draw();
    }
}
