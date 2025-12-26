package fr.seeden.compass;

import fr.seeden.core.math.MathUtil;
import fr.seeden.core.math.Point2;
import fr.seeden.core.math.Vector2;

public class CompassHandler {

    public static final int ANGLE_LIMIT = 70;
    public static final int MIN_ANGLE = -ANGLE_LIMIT;
    public static final int MAX_ANGLE = ANGLE_LIMIT;

    public static final int BAR_WIDTH = 100;
    public static final int BAR_HEIGHT = 10;
    public static final int COMPASS_DOT_SIZE = 10;

    // The player position in the world and the position of the current goal (e.g. a quest)
    private Point2 playerPos, goalPos, lootAtPos;
    // The direction the player is looking at
    private Vector2 playerLookAtVec;

    public int calc(){
        if(playerPos==null || goalPos==null || playerLookAtVec ==null) return 0;
        Vector2 playerGoalVec = new Vector2(this.playerPos, this.goalPos);
        double scalar = this.playerLookAtVec.normalizeDot(playerGoalVec);
        System.out.println(playerLookAtVec +"/"+playerGoalVec+"/"+ scalar +"\n--------------");

        // Scalar=1 means both vectors are facing the same direction. This condition allows a very small margin to avoid unnecessary
        // operations if the direction vector is very close to facing the goal.
        boolean facingGoal = scalar>0.9999;
        double angle = 0;
        if(!facingGoal){
            double radianAngle = Math.acos(scalar);
            double degreeAngle = Math.toDegrees(radianAngle);
            // Rotate the direction vector by the angle limit of the compass (before reaching the end of the bar)
            // And also in the non-clockwise order by half of the angle limit, that way we can also detect from which
            // side the player is the most turned to when being back to the goal, and thus having the delimiter at the
            // perfect middle between left and right, allowing the compass dot to be on the right side every moment
            double newAngle = Math.toDegrees(Math.acos(this.playerLookAtVec.rot(ANGLE_LIMIT).normalizeDot(playerGoalVec)));
            double v = this.playerLookAtVec.rot((double) -ANGLE_LIMIT/2).normalizeDot(playerGoalVec);
            System.out.printf("\nRotated=%.2f ; S=%.2f\n", newAngle, v);
            // If the rotated vector angle exceeds the limit, it means the original angle should be negative in order to
            // move the compass dot on the right side
            if (newAngle > ANGLE_LIMIT && v>=-0.9) degreeAngle *= -1;
            angle = Math.clamp(degreeAngle, MIN_ANGLE, MAX_ANGLE);
            System.out.printf("\nRadiant=%.2f ; Degree=%.2f ; Clamped=%.2f", radianAngle, degreeAngle, angle);
        }
        /*
        Calculate the compass dot X position with the angle.
        Using inverseLerp to get a ratio between 0 and 1 from the angle between its bounds.
        This ratio can now be converted to the X position of the compass dot between the size of the bar using lerp.

        Ex: angle of 0 in the bounds of -70 and 70, giving a 0.5 ratio with inverseLerp.
        Then, using lerp with this ratio, within a +/- limit being half-size of the compass bar width.
        So, 100/2 => +/-50, thus giving 0. Indeed, facing the goal means the dot being at the center of the bar.
        */
        double ratio = MathUtil.inverseLerp(MIN_ANGLE, MAX_ANGLE, angle);
        System.out.printf("Angle=%.2f / min-max=%d~%d / t=%f \n", angle, MIN_ANGLE, MAX_ANGLE, ratio);
        return (int) MathUtil.lerp(-BAR_WIDTH/2, BAR_WIDTH/2, ratio);
    }

    public void reset(){
        playerPos = goalPos = null;
        playerLookAtVec = null;
    }

    public void setPlayerPos(Point2 playerPos) {
        this.playerPos = playerPos;
    }
    public void setGoalPos(Point2 goalPos) {
        this.goalPos = goalPos;
    }
    public void setLootAt(Point2 lootAtPos) {
        if(this.playerPos==null) return;
        this.lootAtPos = lootAtPos;
        this.playerLookAtVec = new Vector2(this.playerPos, lootAtPos);
    }

    public Point2 getPlayerPos() {
        return playerPos;
    }

    public Point2 getGoalPos() {
        return goalPos;
    }

    public Point2 getLootAtPos() {
        return lootAtPos;
    }

    public Vector2 getPlayerLookAtVec() {
        return playerLookAtVec;
    }
}