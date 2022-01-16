package fr.takehere.spacewarmultiplayer;

import fr.takehere.ethereal.Game;
import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.utils.MathUtils;
import fr.takehere.ethereal.utils.RessourcesManager;
import fr.takehere.ethereal.utils.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpacewarMulti extends Game {
    private static SpacewarMulti instance;

    public List<String> shootSounds;
    public List<String> explosionSounds;

    public Actor player;

    float rotationSpeed = 5;
    float speedAddition = 0.3f;
    float velocityDeceleration = (float) (1/1.05);

    private ClientConnection cc;

    @Override
    public void init() {
        instance = this;

        //Resources initialization

        shootSounds = new ArrayList<>();
        explosionSounds = new ArrayList<>();

        RessourcesManager.addImage("bullet", "images/bullet.png", getClass());
        RessourcesManager.addImage("player", "images/player.png", getClass());
        RessourcesManager.addImage("smoke", "images/smoke.png", getClass());
        RessourcesManager.addImage("fire", "images/fire.png", getClass());
        RessourcesManager.addImage("asteroid", "images/asteroid.png", getClass());
        RessourcesManager.addImage("menu", "images/menu.png", getClass());

        shootSounds.add("sounds/shoot1.wav");
        shootSounds.add("sounds/shoot2.wav");
        shootSounds.add("sounds/shoot3.wav");

        explosionSounds.add("sounds/explosion1.wav");
        explosionSounds.add("sounds/explosion2.wav");
        explosionSounds.add("sounds/explosion3.wav");

        //Game Initialization
        player = new Actor(new Vector2(250,250), new Dimension(40,40), RessourcesManager.getImage("player"), "player", this);

        connectToServer();
    }

    @Override
    public void gameLoop(double v) {
        //----< Drawing Background >----
        Graphics2D g2d = gameWindow.getGraphics();
        g2d.setColor(new Color(26, 33, 84));
        g2d.fillRect(0,0, gameWindow.getWidth(), gameWindow.getHeight());

        //----< Player friction >----
        player.velocity = player.velocity.multiply(velocityDeceleration);

        //----< Player rotation >----
        if (gameWindow.isPressed(KeyEvent.VK_LEFT)){
            player.rotation -= rotationSpeed;
        }else if (gameWindow.isPressed(KeyEvent.VK_RIGHT)){
            player.rotation += rotationSpeed;
        }

        //----< Player Speed >----
        if (gameWindow.isPressed(KeyEvent.VK_UP)){
            player.velocity = player.velocity.add(MathUtils.getForwardVector(player.rotation).multiply(speedAddition));
        }else if (gameWindow.isPressed(KeyEvent.VK_DOWN)){
            player.velocity = player.velocity.add(MathUtils.getForwardVector(player.rotation).multiply(-1).multiply(speedAddition));
        }

        //----< Player teleportation if oob >----
        if (player.location.x > gameWindow.canvas.getWidth()) player.location.x = 0;
        if (player.location.x < 0) player.location.x = gameWindow.canvas.getWidth();

        if (player.location.y > gameWindow.canvas.getHeight()) player.location.y = 0;
        if (player.location.y < 0) player.location.y = gameWindow.canvas.getHeight();
    }

    public void connectToServer(){
        cc = new ClientConnection();
        Thread t = new Thread(cc);
        t.start();
    }

    public SpacewarMulti(String title, int height, int width, int targetFps) {
        super(title, height, width, targetFps);
    }
    public static void main(String[] args) {
        new SpacewarMulti("Multiplayer Spacewar", 500,500, 60);
    }

    public static SpacewarMulti getInstance() {
        return instance;
    }
}
