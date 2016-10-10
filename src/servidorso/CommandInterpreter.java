/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorso;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kevin
 */
public class CommandInterpreter {
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int ERROR_INVALID_COMMAND_CODE = -2;
    
    public static final int COMMAND_AUTOMATIC_MODE = 1;
    public static final int COMMAND_MANULA_MODE = 2;
    public static final int COMMAND_MOVE_FORWARD = 3;
    public static final int COMMAND_TURN_RIGHT = 4;
    public static final int COMMAND_TURN_LEFT = 5;
    public static final int COMMAND_MOVE_BACK = 6;
    public static final int COMMAND_TOMAR_FOTO = 7;
    
    private final Car mCar;
    
    public CommandInterpreter(Car car){
        mCar = car;
    }
    
    public String executeCommand(int commandCode){
        switch(commandCode){
            case COMMAND_AUTOMATIC_MODE:
                mCar.setMode(Car.MODE_AUTOMATIC);
                new AutomaticModeThread().start();
                return buildCommandResponse(SUCCESS, COMMAND_AUTOMATIC_MODE, null);
            case COMMAND_MANULA_MODE: 
                mCar.setMode(Car.MODE_MANUAL);
                return buildCommandResponse(SUCCESS, COMMAND_MANULA_MODE, null);
            case COMMAND_MOVE_FORWARD:
                if(mCar.getMode() == Car.MODE_MANUAL){
                    mCar.move();
                    return buildMoveResponse(Car.FORWARD);
                }else{
                    return buildCommandResponse(ERROR, COMMAND_MOVE_FORWARD, "Car is in automatic mode, change mode to manual.");
                }
            case COMMAND_TURN_LEFT:
                if(mCar.getMode() == Car.MODE_MANUAL){
                    mCar.turn(Car.LEFT);
                    return buildMoveResponse(Car.LEFT);
                }else{
                    return buildCommandResponse(ERROR, COMMAND_MOVE_FORWARD, "Car is in automatic mode, change mode to manual.");
                }
            case COMMAND_TURN_RIGHT:
                if(mCar.getMode() == Car.MODE_MANUAL){
                    mCar.turn(Car.RIGHT);
                    return buildMoveResponse(Car.RIGHT);
                }else{
                    return buildCommandResponse(ERROR, COMMAND_MOVE_FORWARD, "Car is in automatic mode, change mode to manual.");
                }
            case COMMAND_MOVE_BACK:
                if(mCar.getMode() == Car.MODE_MANUAL){
                    mCar.moveBack();
                    return buildMoveResponse(Car.BACK);
                }else{
                    return buildCommandResponse(ERROR, COMMAND_MOVE_FORWARD, "Car is in automatic mode, change mode to manual.");
                }
            case COMMAND_TOMAR_FOTO:
               String result = mCar.sonar(3) ? "Se encontro una pared!!" : "Puedes avanzar :D";
               return buildCommandResponse(SUCCESS, COMMAND_TOMAR_FOTO, result);
            default: 
                return buildCommandResponse(ERROR_INVALID_COMMAND_CODE, commandCode, "Not a valid command.");
        }
    }
    
    private String buildCommandResponse(int resultCode, int commandCode, String message){
        if(resultCode == 0){
            return "{\"command\":" + commandCode + ", \"result_code\":" + resultCode + 
                    ", \"message\":\"" + message + "\"}";
        } else {
            if( message == null || message.equals("") )
                message = "Unhandled error";
            return "{\"command\":" + commandCode + ", \"result_code\":" + resultCode + 
                    ", \"message\":\"" + message + "\"}";
        }
    }
    
    private String buildMoveResponse(int move){
        return "{\"action\":" + move + "}";
    }
    
    private class AutomaticModeThread extends Thread{
        
        @Override
        public void run(){
            mCar.solveMaze();
        }
    }
}
