package fr.seeden.compass;

import fr.seeden.core.math.MathUtil;
import fr.seeden.core.math.Vector2;

public class CompassHandler {

    private CompassPanel compassPanel;
    public CompassHandler(CompassPanel compassPanel){
        this.compassPanel = compassPanel;
    }

    public void calc(){
        if(compassPanel.playerPos==null || compassPanel.lookPoint==null || compassPanel.goalPos==null) return;
        Vector2 playerLookVec = new Vector2(compassPanel.playerPos, compassPanel.lookPoint);
        Vector2 playerGoalVec = new Vector2(compassPanel.playerPos, compassPanel.goalPos);
        double scalar = playerLookVec.normalizeDot(playerGoalVec);
        System.out.println(playerLookVec+"/"+playerGoalVec+"/"+ scalar +"\n--------------");

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
            double newAngle = Math.toDegrees(Math.acos(playerLookVec.rot(CompassPanel.ANGLE_LIMIT).normalizeDot(playerGoalVec)));
            double v = playerLookVec.rot((double) -CompassPanel.ANGLE_LIMIT/2).normalizeDot(playerGoalVec);
            System.out.printf("\nRotated=%.2f ; S=%.2f\n", newAngle, v);
            // If the rotated vector angle exceeds the limit, it means the original angle should be negative in order to
            // move the compass dot on the right side
            if (newAngle > CompassPanel.ANGLE_LIMIT && v>=-0.9) degreeAngle *= -1;
            angle = Math.clamp(degreeAngle, CompassPanel.MIN_ANGLE, CompassPanel.MAX_ANGLE);
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
        double ratio = MathUtil.inverseLerp(CompassPanel.MIN_ANGLE, CompassPanel.MAX_ANGLE, angle);
        System.out.printf("Angle=%.2f / min-max=%d~%d / t=%f \n", angle, CompassPanel.MIN_ANGLE, CompassPanel.MAX_ANGLE, ratio);
        compassPanel.compassX = (int) ((double) compassPanel.panelWidth /2 + MathUtil.lerp(-CompassPanel.BAR_WIDTH/2, CompassPanel.BAR_WIDTH/2, ratio));
    }

    /*public void resetPoints(){
        playerPos = lookPoint = goalPos = null;
    }

    public void setPlayerPos(Point2 playerPos) {
        this.playerPos = playerPos;
    }
    public void setLookPoint(Point2 lookPoint) {
        this.lookPoint = lookPoint;
    }
    public void setGoalPos(Point2 goalPos) {
        this.goalPos = goalPos;
    }*/
}