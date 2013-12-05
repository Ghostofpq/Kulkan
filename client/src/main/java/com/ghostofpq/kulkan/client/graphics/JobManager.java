package com.ghostofpq.kulkan.client.graphics;


import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JobManager extends HUDElement {

    private Job job;
    private Map<Capacity, Button> capacities;
    private Map<Integer, List<Capacity>> tierCapacityMap;
    private Capacity selectedCapacity;

    public JobManager(int posX, int posY, int width, int height, Job job) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.job = job;
        tierCapacityMap = new HashMap<Integer, List<Capacity>>();
        capacities = new HashMap<Capacity, Button>();
        selectedCapacity = null;
        for (Capacity capacity : job.getSkillTree()) {
            if (null == tierCapacityMap.get(capacity.getMaxDepth())) {
                tierCapacityMap.put(capacity.getMaxDepth(), new ArrayList<Capacity>());
            }
            tierCapacityMap.get(capacity.getMaxDepth()).add(capacity);
        }

        int maxNumberOfTierCapacity = 0;
        for (Integer i : tierCapacityMap.keySet()) {
            maxNumberOfTierCapacity = Math.max(maxNumberOfTierCapacity, tierCapacityMap.get(i).size());
        }

        for (Integer i : tierCapacityMap.keySet()) {
            for (Capacity capacity : tierCapacityMap.get(i)) {
                placeCapacity(capacity);
            }
        }
    }

    public void placeCapacity(Capacity capacity) {
        if (capacities.keySet().contains(capacity)) {
            log.debug("{} is already placed", capacity.getName());
        } else {
            log.debug("placing {}", capacity.getName());
            int capacitySlot = 0;
            for (Capacity capacityPlaced : capacities.keySet()) {
                if (capacityPlaced.getMaxDepth() == capacity.getMaxDepth()) {
                    capacitySlot++;
                }
            }

            int slotWidth = (this.width / tierCapacityMap.get(capacity.getMaxDepth()).size());
            int slotHeight = (this.height / 5);
            int posXButton = posX + (slotWidth / 5) + capacitySlot * slotWidth;
            int posYButton = posY + slotHeight * capacity.getMaxDepth();
            int buttonWidth = 3 * (slotWidth / 5);
            int buttonHeigth = 2 * (slotHeight / 3);
            String buttonLabel = capacity.getName();
            if (capacity.isAvailable()) {
                buttonLabel = new StringBuilder().append(buttonLabel).append("_").append("Av").toString();
            }
            if (capacity.isLocked()) {
                buttonLabel = new StringBuilder().append(buttonLabel).append("_").append("Lc").toString();
            }
            Button buttonCapacity = new Button(posXButton, posYButton, buttonWidth, buttonHeigth, buttonLabel) {
                @Override
                public void onClick() {

                }
            };
            capacities.put(capacity, buttonCapacity);

            for (Capacity sons : capacity.getSons()) {
                placeCapacity(sons);
            }
        }
    }

    @Override
    public void draw() {
        for (Button buttonCapacity : capacities.values()) {
            buttonCapacity.draw();
        }
    }

    public Capacity clickedCapacity(int mouseX, int mouseY) {
        Capacity clickedCapacity = null;

        for (Capacity capacity : capacities.keySet()) {
            if (capacities.get(capacity).isClicked(mouseX, mouseY)) {
                clickedCapacity = capacity;
            }
        }

        return clickedCapacity;
    }

}
