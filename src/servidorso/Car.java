/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorso;

import com.pi4j.wiringpi.SoftPwm;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kevin
 */
public class Car {

    // possible moves
    public static final int FORWARD = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final int BACK = 4;

    // possible turns
    public static final int RIGTH_MOTOR = 1;
    public static final int LEFT_MOTOR = 2;

    // modes
    public static final int MODE_AUTOMATIC = 1;
    public static final int MODE_MANUAL = 2;

    // El modo en el que se encuentra el carro
    private int mMode;

    // Los pines que usara de la Raspberry
    // se requiern dos por motor.
    private static final int MOTOR1_PIN1 = 21;
    private static final int MOTOR1_PIN2 = 22;
    private static final int MOTOR2_PIN1 = 23;
    private static final int MOTOR2_PIN2 = 24;

    //Sentido de giro
    private static final int CLOCKWISE = 1;
    private static final int COUNTERCLOCKWISE = 2;

    // Configuracion del carro
    private int left_turn_time;
    private int right_turn_time;
    private int move_time;
    private int left_wheel_power;
    private int right_wheel_power;

    //Camara
    private final Camara mCamara;

    public Car() {
        mMode = MODE_MANUAL;

        left_turn_time = 300;
        right_turn_time = 300;
        move_time = 300;
        left_wheel_power = 100;
        right_wheel_power = 100;

        // inicializacion de pines
        com.pi4j.wiringpi.Gpio.wiringPiSetup();
        SoftPwm.softPwmCreate(MOTOR1_PIN1, 0, 100);
        SoftPwm.softPwmCreate(MOTOR1_PIN2, 0, 100);
        SoftPwm.softPwmCreate(MOTOR2_PIN1, 0, 100);
        SoftPwm.softPwmCreate(MOTOR2_PIN2, 0, 100);

        mCamara = new Camara();
    }

    /**
     * Change car state
     *
     * @param direction
     * {@link #NORTH} | {@link #WEST} | {@link #SOUTH} | {@link #EAST}
     */
    public void turn(int direction) {
        if (direction == RIGHT) {
            motorOn(LEFT_MOTOR, CLOCKWISE, left_wheel_power);
            motorOn(RIGTH_MOTOR, COUNTERCLOCKWISE, right_wheel_power);
            delay(right_turn_time);
            motorOff(LEFT_MOTOR);
            motorOff(RIGTH_MOTOR);
        } else if (direction == LEFT) {
            motorOn(RIGTH_MOTOR, CLOCKWISE, right_wheel_power);
            motorOn(LEFT_MOTOR, COUNTERCLOCKWISE, left_wheel_power);
            delay(left_turn_time);
            motorOff(RIGTH_MOTOR);
            motorOff(LEFT_MOTOR);
        }
    }

    public void move() {
        motorOn(RIGTH_MOTOR, CLOCKWISE, right_wheel_power);
        motorOn(LEFT_MOTOR, CLOCKWISE, left_wheel_power);
        delay(move_time);
        motorOff(RIGTH_MOTOR);
        motorOff(LEFT_MOTOR);
    }

    void moveBack() {
        motorOn(RIGTH_MOTOR, COUNTERCLOCKWISE, 100);
        motorOn(LEFT_MOTOR, COUNTERCLOCKWISE, 70);
        delay(move_time);
        motorOff(RIGTH_MOTOR);
        motorOff(LEFT_MOTOR);
    }

    public void motorOn(int motor, int sentido, int power) {
        if (motor == LEFT_MOTOR) {
            //TODO TURN ON RIGHT MOTOR
            motorOn(MOTOR1_PIN1, MOTOR1_PIN2, sentido, power);
        } else {
            //TODO TURN ON LEFT MOTOR
            motorOn(MOTOR2_PIN1, MOTOR2_PIN2, sentido, power);
        }
    }

    private void motorOff(int motor) {
        if (motor == RIGTH_MOTOR) {
            motorOff(MOTOR2_PIN1, MOTOR2_PIN2);
        } else {
            motorOff(MOTOR1_PIN1, MOTOR1_PIN2);
        }
    }

    private void motorOn(int pin1, int pin2, int sentido, int power) {
        if (sentido == CLOCKWISE) {
            SoftPwm.softPwmWrite(pin1, power);
            SoftPwm.softPwmWrite(pin2, 0);
        } else {
            SoftPwm.softPwmWrite(pin2, power);
            SoftPwm.softPwmWrite(pin1, 0);
        }
    }

    private void motorOff(int pin1, int pin2) {
        SoftPwm.softPwmWrite(pin1, 0);
        SoftPwm.softPwmWrite(pin2, 0);
    }

    private void delay(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ex) {
            Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets car's mode
     *
     * @return {@link #MODE_AUTOMATIC} | {@link #MODE_MANUAL}
     */
    public int getMode() {
        return mMode;
    }

    /**
     * Sets car's mode. ({@link #MODE_AUTOMATIC} | {@link #MODE_MANUAL})
     *
     * @param mode
     */
    public void setMode(int mode) {
        if (mode != MODE_AUTOMATIC && mode != MODE_MANUAL) {
            return;
        }
        this.mMode = mode;
    }

    void setLeftTurnTime(int time) {
        if (time > 0) {
            left_turn_time = time;
        }
    }

    void setRightTurnTime(int time) {
        if (time > 0) {
            right_turn_time = time;
        }
    }

    void setLeftWheelPower(int power) {
        if (0 <= power && power <= 100) {
            left_wheel_power = power;
        }
    }

    void setRightWheelPower(int power) {
        if (0 <= power && power <= 100) {
            right_wheel_power = power;
        }
    }

    void setMoveTime(int time) {
        if (0 < time) {
            move_time = time;
        }
    }

    public Boolean tune(String input) {
        if (input.startsWith("c")) {
            String[] settings = input.split(" ");
            setLeftTurnTime(Integer.valueOf(settings[1]));
            setRightTurnTime(Integer.valueOf(settings[2]));
            setLeftWheelPower(Integer.valueOf(settings[3]));
            setRightWheelPower(Integer.valueOf(settings[4]));
            setMoveTime(Integer.valueOf(settings[5]));

            return true;
        }

        return false;
    }

    public Boolean sonar() {
        Boolean result = null;
        mCamara.tomarFoto();
        try {
            result = AnalizarImagen.analizarImagen();
        } catch (Exception ex) {
            Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(result == null){
            return sonar();
        }
        
        return result;
    }

    public void solveMaze() {
        while (getMode() == Car.MODE_AUTOMATIC) {
            turn(LEFT);
            boolean paredIzquierda = sonar();
            if (paredIzquierda == true) {
                turn(RIGHT);
                boolean paredFrontal = sonar();
                if (paredFrontal == true) {
                    turn(RIGHT);
                    boolean paredDerecha = sonar();
                    if (paredDerecha == true) {
                        turn(RIGHT);
                    }
                }
            }
            delay(1000);
            move();
            delay(1000);
        }
        System.out.println("Automatic mode went off!! <------------------");
    }

    private void nextMove() {

    }
}
