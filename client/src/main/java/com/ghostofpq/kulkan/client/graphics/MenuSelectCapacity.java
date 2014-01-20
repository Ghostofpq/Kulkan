package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import lombok.extern.slf4j.Slf4j;
import org.newdawn.slick.Color;

import java.util.List;

@Slf4j
public class MenuSelectCapacity {

    private final String FONT = "optimus_princeps_16";
    private int posX;
    private int posY;
    private int frameWidth;
    private int frameLength;
    private int frameHeight;
    private int index;
    private List<Move> moveList;
    private int currentMana;

    private FontManager fontManager = FontManager.getInstance();

    public MenuSelectCapacity(int posX, int posY, int frameLength, int frameHeight, int frameWidth, List<Move> moveList, int currentMana) {
        this.posX = posX;
        this.posY = posY;
        this.moveList = moveList;
        this.currentMana = currentMana;
        this.frameLength = frameLength;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;

        for (Move move : moveList) {
            log.debug(move.getName() + "//" + move.getDescription());
        }
    }

    public void render(Color color) {
        if (!moveList.isEmpty()) {
            Toolbox.drawFrame(posX, posY, frameLength, frameHeight, frameWidth, color);
            int optionHeight = frameHeight / (moveList.size() + 2);

            int optionY = posY;
            optionY += optionHeight;
            for (int i = 0; i < moveList.size(); i++) {
                int optionX = posX + ((frameLength - fontManager.getFontMap().get(FONT).getWidth(moveList.get(i).getName())) / 2);
                if (moveList.get(index).equals(moveList.get(i))) {
                    fontManager.drawString(FONT, optionX, optionY, moveList.get(i).getName(), Color.yellow);
                } else {
                    if (moveList.get(i).getManaCost() <= currentMana) {
                        fontManager.drawString(FONT, optionX, optionY, moveList.get(i).getName(), Color.white);
                    } else {
                        fontManager.drawString(FONT, optionX, optionY, moveList.get(i).getName(), Color.gray);
                    }
                }
                optionY += optionHeight;
            }
        }
    }

    public void incrementOptionsIndex() {
        if (index >= moveList.size() - 1) {
            index = 0;
        } else {
            index++;
        }
    }

    public void decrementOptionsIndex() {
        if (index <= 0) {
            index = moveList.size() - 1;
        } else {
            index--;
        }
    }

    public Move getSelectedOption() {
        return moveList.get(index);
    }
}
