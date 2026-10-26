package dev.gazesnatchers.client.geo;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BabyGazeSnatcherGeoModel extends DefaultedEntityGeoModel<BabyGazeSnatcherEntity> {
    public BabyGazeSnatcherGeoModel() {
        super(GazeSnatchers.id("baby_gaze_snatcher"), "head");
    }
}
