package kgu.game.project;
import com.badlogic.gdx.utils.TimeUtils;
import kgu.game.project.managers.MemoryManager;

import java.util.ArrayList;


public class GameSession {

    public GameState state;
    long nextTrashSpawnTime;
    long nextMonsterSpawnTime;
    long sessionStartTime;
    long pauseStartTime;
    private int score;
    int destructedTrashNumber;

    public GameSession() {
    }

    public void startGame() {
        state = GameState.PLAYING;
        score = 0;
        destructedTrashNumber = 0;
        sessionStartTime = TimeUtils.millis();
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        sessionStartTime += TimeUtils.millis() - pauseStartTime;
    }

    public void endGame() {
        updateScore();
        state = GameState.ENDED;
        ArrayList<Integer> recordsTable = MemoryManager.loadRecordsTable();
        if (recordsTable == null) {
            recordsTable = new ArrayList<>();
        }
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx) < getScore()) break;
        }
        recordsTable.add(foundIdx, getScore());
        MemoryManager.saveTableOfRecords(recordsTable);
    }

    public void destructionRegistration() {
        destructedTrashNumber += 1;
    }

    public void updateScore() {
        score = (int) (TimeUtils.millis() - sessionStartTime) / 100 + destructedTrashNumber * 100;
    }

    public int getScore() {
        return score;
    }
}
