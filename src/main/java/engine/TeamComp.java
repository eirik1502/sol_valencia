package engine;

/**
 * Created by eirik on 01.08.2017.
 */
public class TeamComp implements Component {

    public int team;
    public int idOnTeam;

    public TeamComp(int team, int idOnTeam) {
        this.team = team;
        this.idOnTeam = idOnTeam;
    }
}
