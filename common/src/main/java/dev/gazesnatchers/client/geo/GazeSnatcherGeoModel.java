package dev.gazesnatchers.client.geo;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GazeSnatcherGeoModel extends DefaultedEntityGeoModel<GazeSnatcherEntity> {
    public GazeSnatcherGeoModel() {
        super(GazeSnatchers.id("gaze_snatcher"), "head");
    }
}
