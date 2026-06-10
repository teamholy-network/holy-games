package de.teamholy.clutches.playground.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@NoArgsConstructor
@Setter
public class Hit {

    public static final double MIN_XKNOCK = 0.8, MAX_XKNOCK = 6;
    public static final double MIN_YKNOCK = 0.353, MAX_YKNOCK = 2;

    private double xknock = 1.2;
    private double yknock = 0.373;
    private Icon icon = Icon.YELLOW_WOOL;
    private DiagonalDirection diagonalDirection = DiagonalDirection.STRAIGHT;

    public void setXknock(double xknock) {
        this.xknock = clamp(xknock, MIN_XKNOCK, MAX_XKNOCK);
    }

    public void setYknock(double yknock) {
        this.yknock = clamp(yknock, MIN_YKNOCK, MAX_YKNOCK);
    }

   public double getXknock() {
        return clamp(xknock, MIN_XKNOCK, MAX_XKNOCK);
    }

    public double getYknock() {
        return clamp(yknock, MIN_YKNOCK, MAX_YKNOCK);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }


    @AllArgsConstructor
    @Getter
    public enum Icon {
        GREEN_WOOL(Material.WOOL, 5),
        YELLOW_WOOL(Material.WOOL, 4),
        RED_WOOL(Material.WOOL, 14),
        BLUE_WOOL(Material.WOOL, 11),
        PURPLE_WOOL(Material.WOOL, 10),
        LG_WOOL(Material.WOOL, 8),
        BLACK_WOOL(Material.WOOL, 15),

        GREEN_STAINED_GLASS(Material.STAINED_GLASS, 5),
        YELLOW_STAINED_GLASS(Material.STAINED_GLASS, 4),
        RED_STAINED_GLASS(Material.STAINED_GLASS, 14),
        BLUE_STAINED_GLASS(Material.STAINED_GLASS, 11),
        PURPLE_STAINED_GLASS(Material.STAINED_GLASS, 10),
        LIGHT_GRAY_STAINED_GLASS(Material.STAINED_GLASS, 8),
        BLACK_STAINED_GLASS(Material.STAINED_GLASS, 15),

        GREEN_STAINED_CLAY(Material.STAINED_CLAY, 5),
        YELLOW_STAINED_CLAY(Material.STAINED_CLAY, 4),
        RED_STAINED_CLAY(Material.STAINED_CLAY, 14),
        BLUE_STAINED_CLAY(Material.STAINED_CLAY, 11),
        PURPLE_STAINED_CLAY(Material.STAINED_CLAY, 10),
        LG_STAINED_CLAY(Material.STAINED_CLAY, 8),
        BLACK_STAINED_CLAY(Material.STAINED_CLAY, 15),

        STICK(Material.STICK,0),
        WOODSTAIR(Material.WOOD_STAIRS,0),
        STEP(Material.WOOD_STEP,0),
        STONESTAIR(Material.COBBLESTONE_STAIRS,0),
        STONESTEP(Material.STONE_SLAB2,0);


        Material material;
        int subId;
    }

    @AllArgsConstructor @Getter
    public enum DiagonalDirection {
        STRAIGHT("Straight / Normal Hit"),
        LEFT("Diagonal left"),
        RIGHT("Diagonal right");


        private String name;
    }
}
