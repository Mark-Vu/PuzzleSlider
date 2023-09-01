package org.example.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    @Test
    void extractNumberTest1 () {
        var gameLogic = new GameLogic() ;
        assertEquals(8 , gameLogic.extractNumber("8 D"));

    }

    @Test
    void extractNumberTest2 () {
        var gameLogic = new GameLogic() ;
        assertEquals(100, gameLogic.extractNumber("100 D"));

    }

    @Test
    void extractNumberTest3 () {
        var gameLogic = new GameLogic() ;
        assertEquals(60 , gameLogic.extractNumber("60 U"));

    }

    @Test
    void extractNumberTest4 () {
        var gameLogic = new GameLogic() ;
        assertEquals(-1 , gameLogic.extractNumber(" D"));

    }

    @Test
    void extractNumberTest5 () {
        var gameLogic = new GameLogic() ;
        assertEquals(-1 , gameLogic.extractNumber(""));

    }

    @Test
    void extractMove1() {
        var gameLogic = new GameLogic() ;
        assertEquals('D',gameLogic.extractMove("8 D"));
    }

    @Test
    void extractMove2() {
        var gameLogic = new GameLogic() ;
        assertEquals('U',gameLogic.extractMove("100 U"));
    }
    @Test
    void extractMove3() {
        var gameLogic = new GameLogic() ;
        assertEquals('D',gameLogic.extractMove("100000 D"));
    }
    @Test
    void extractMove4() {
        var gameLogic = new GameLogic() ;
        assertEquals('\0',gameLogic.extractMove("10000"));
    }
    @Test
    void extractMove5 () {
        var gameLogic = new GameLogic() ;
        assertEquals('\0',gameLogic.extractMove(""));
    }



}