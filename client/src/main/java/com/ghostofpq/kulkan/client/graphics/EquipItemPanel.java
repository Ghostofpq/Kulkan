package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.HUDElement;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EquipItemPanel extends HUDElement {
    private Item selectedItem;
    private List<Item> itemList;
    private List<Button> itemButtonList;


    public EquipItemPanel(int posX, int posY, int width, int height, List<Item> itemList) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.itemList = itemList;
        init();
    }


    public void init() {
        int widthSeparator = width / 20;
        int heightSeparator = height / 20;
        int widthStep = (width - 5 * widthSeparator) / 5;
        int heightStep = (height - 3 * heightSeparator) / 3;

        itemButtonList = new ArrayList<Button>();

        for (int i = 0; i < itemList.size(); i++) {
            final Item item = itemList.get(i);
            int line = i / 5;
            int col = i % 5;
            Button itemButton = new Button(posX + (col + 1) * widthSeparator + (col) * widthStep, posY + (1 + line) * heightSeparator + (1 + line) * heightStep, widthStep, heightStep, item.getName()) {
                @Override
                public void onClick() {
                    selectItem(item);
                }
            };
            itemButtonList.add(itemButton);
        }
        this.selectedItem = null;
    }


    public void selectItem(Item item) {
        selectedItem = item;
    }

    public Item getClickedItem() {
        selectedItem = null;
        for (Button button : itemButtonList) {
            if (button.isClicked()) {
                button.onClick();
                break;
            }
        }
        return selectedItem;
    }

    @Override
    public void draw() {
        for (Button button : itemButtonList) {
            button.draw();
        }
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        init();
    }
}
