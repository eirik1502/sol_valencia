package engine.character;

import engine.*;
import engine.combat.DamageableComp;
import engine.combat.abilities.AbilityComp;

import engine.graphics.view_.ViewControlComp;

import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.text.TextMeshComp;

import engine.physics.*;
import game.GameUtils;
import utils.maths.M;
import utils.maths.TrigUtils;
import utils.maths.Vec2;
import utils.maths.Vec4;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterSys implements Sys {


    private WorldContainer wc;




    public CharacterSys() {

    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;

        //allocate bullet entity

    }

    @Override
    public void update() {
        //float aimAngle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(),   userInput.getMouseX(), userInput.getMouseY());

        int charNumb = 0;

        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {

            updateEntity(entity, charNumb);

            charNumb++;
        }
    }

    @Override
    public void terminate() {

    }

    private void updateEntity(int entity, int charNumb) {
        CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);
        PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
        CharacterInputComp inputComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
        RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
        PhysicsComp phComp = (PhysicsComp) wc.getComponent(entity, PhysicsComp.class);
        AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);
        DamageableComp dmgableComp = (DamageableComp)wc.getComponent(entity, DamageableComp.class);
        AffectedByHoleComp affholeComp = (AffectedByHoleComp)wc.getComponent(entity, AffectedByHoleComp.class);
        TeamComp teamComp = (TeamComp) wc.getComponent(entity, TeamComp.class);
        //        TextMeshComp textComp = (TextMeshComp)wc.getComponent(entity, TextMeshComp.class);



        //returns false if we are in respawn delay
        if ( !checkHoleAffected(charNumb, teamComp, posComp, phComp, charComp, dmgableComp, affholeComp) ) {
            return;
        }

//        updateDisplayDamage(charNumb, dmgableComp, textComp);


        //do not take input if character is executing ability or is stunned
        if (abComp.getOccupiedBy() != null) return;
        if (dmgableComp.isStunned()) return;

        updateMove(charComp, inputComp, phComp);
        updateRotation(entity, inputComp, posComp, rotComp);
        updateAbilities(entity, charComp, abComp, inputComp, posComp, rotComp);
    }

    private boolean checkHoleAffected(int charNumb, TeamComp teamComp, PositionComp posComp, PhysicsComp physComp, CharacterComp charComp, DamageableComp dmgablComp, AffectedByHoleComp affholeComp) {
        //decrease respawn timer if it is above 0
        //respawn character if it is 1
        //set timer if character fell in hole
        if (charComp.respawnTimer > 0) {
            --charComp.respawnTimer;

            return false;
        }
        else if (charComp.respawnTimer == 0) {
            charComp.respawnTimer = -1;

            //respawn the character
            Vec2 respawnPos = GameUtils.teamStartPos[teamComp.team][0];

            dmgablComp.reset();
            physComp.reset();

            charComp.incrementRespawnCount();

            posComp.setPos(respawnPos);

            System.out.println("Character numb: "+charNumb+" stocks lost: "+charComp.getRespawnCount());

            return false;
        }

        else if (affholeComp.isHoleAffectedFlag()) {

            charComp.respawnTimer = charComp.respawnTime;

        }

        return true;
    }


    private void updateMove(CharacterComp charComp, CharacterInputComp inputComp, PhysicsComp phComp) {

        float accel = charComp.getMoveAccel();
        float stepX = ( (inputComp.isMoveRight()? 1:0) - (inputComp.isMoveLeft()? 1:0) );
        float stepY = ( (inputComp.isMoveDown()? 1:0) - (inputComp.isMoveUp()? 1:0) );

        phComp.addAcceleration(new Vec2(stepX, stepY).normalize().scale(accel));
    }

    private void updateRotation(int entity, CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        Vec2 posInView = new Vec2(0,0);
        if (wc.hasComponent(entity, ViewControlComp.class)) {
            ViewControlComp viewComp = (ViewControlComp) wc.getComponent(entity, ViewControlComp.class);
            posInView = viewComp.getViewOffset().negative();
        }

        float newAngle = TrigUtils.pointDirection( posInView, new Vec2( inputComp.getAimX(), inputComp.getAimY() )  );


        float diffAngle = TrigUtils.shortesAngleBetween(rotComp.getAngle(), newAngle);

        //add a portion of diffAngle
        rotComp.addAngle(diffAngle * 0.3f);
    }

    private void updateAbilities(int entity, CharacterComp charComp, AbilityComp abComp, CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        //System.out.println(inputComp.isAction1());


        if (inputComp.isAction1()){
            abComp.requestExecution(0);
        }

        if (inputComp.isAction2()) {
            abComp.requestExecution(1);
        }

        if (inputComp.isAction3()) {
            abComp.requestExecution(2);
        }


    }

//    private void updateDisplayDamage(int charNumb, DamageableComp dmgablComp, TextMeshComp textComp) {
//        Vec2[] pos = {new Vec2(50, GameUtils.MAP_HEIGHT-100),
//            new Vec2(GameUtils.MAP_WIDTH-200, GameUtils.MAP_HEIGHT-100) };
//
//        textComp.setSize(64);
//        textComp.setColor(new Vec4(1, 0, 0, 1)); //red
//
//        textComp.setViewX(pos[charNumb].x);
//        textComp.setViewY(pos[charNumb].y);
//        textComp.getTextMesh().setString( Integer.toString((int)dmgablComp.getDamage()) );
//    }


}
