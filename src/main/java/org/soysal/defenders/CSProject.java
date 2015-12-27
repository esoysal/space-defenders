package org.soysal.defenders;

import java.awt.*;

import javax.swing.*; 
import javax.swing.Timer;

import java.awt.event.*;

import javax.imageio.*;

import java.io.*; 
import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.*;

import java.util.*;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.net.*;
import java.io.*;
import java.applet.*;

import javax.net.*;
import javax.sound.sampled.AudioFormat;import javax.sound.sampled.AudioInputStream;import javax.sound.sampled.AudioSystem;import javax.sound.sampled.Clip;import javax.sound.sampled.DataLine;import javax.sound.sampled.LineEvent;import javax.sound.sampled.LineListener;import javax.sound.sampled.LineUnavailableException;import javax.sound.sampled.UnsupportedAudioFileException;

 
public class CSProject 
{
 
 public static void main(String...args) 
 {
 
  JFrame frame = new JFrame();
   GamePanel game = new GamePanel();
   
   game.demoStart();
 
  frame.setSize(900, 600);
   frame.add(game);   
  frame.setVisible(true);
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
 } 
}
 
class GamePanel extends JPanel implements KeyListener, ActionListener 
{
 
 private Player player;
 private ArrayList<Bullet> bullets;
 private int bulletSpeed;
 private ArrayList<Enemy> enemies;
 private int enemySpeed;
 private ArrayList<PowerUp> powers;
 private int powerSpeed;
 private Timer timer;
 private int timerSpeed;
 private BufferedImage background;
 private boolean gameActive;
 private boolean gameFinished;
 private Clip shoot;
 private Clip explosion;
 private int enemiesKilled;
 JButton playButton;

 
  private static final long serialVersionUID = 1552746400473185110L;
  
  
 
 public void demoStart() //displays the beginning screen (backgrounds, buttons etc.), initializes player and timer
 {
  gameActive=false;
  gameFinished=false;
  
  try 
        {
       background = ImageIO.read(GamePanel.class.getResourceAsStream("/org/soysal/defenders/Background2.png"));
  } 
  catch (IOException e) {} 
  
  this.setLayout(null);
  playButton = new JButton("PLAY GAME");
  playButton.setBounds(350, 400, 200, 30);
  playButton.addActionListener(new ActionListener(){

	public void actionPerformed(ActionEvent arg0) {
		playButton.setVisible(false);
		gameStart(5);		
	}	  
  });
  
  this.add(playButton);
  timerSpeed = 15;
  timer = new Timer(timerSpeed, this); 
 
  player = new Player(); 
  
   addKeyListener(this);
   
   loadSound();
 }
 
 public void gameStart(int numEnemy) //initializes other game elements (bullets, enemies etc.), starts game & timer, sets background
 { 
  gameActive=true;
  
  bullets = new ArrayList<Bullet>();
  bulletSpeed = 2;
  enemies = new ArrayList<Enemy>();
  enemySpeed = 1;
  powers = new ArrayList<PowerUp>();
  powerSpeed = 3;
  
  player.reset();  
  
  int enemyIndex;
  
  for (int i=0; i<numEnemy; i++)
  {
   enemyIndex=getEnemyIndex();
   if (!enemyCheck(enemyIndex))
    enemies.add(new Enemy(enemyIndex));
   else
    i--;
  }
    
   //timer.setInitialDelay(pause);
  timer.start(); 
 
        try 
        {
       background = ImageIO.read(GamePanel.class.getResourceAsStream("/org/soysal/defenders/Background.png"));
  } 
  catch (IOException e) {}

 }
   
 public void gameEnd() //displays the "game over" screen when player dies, redisplays buttons, sets background etc.
 {   
  explosion.setFramePosition(0);
  explosion.start();
  
  gameActive=false;
  gameFinished=true;
  
  try 
        {
       background = ImageIO.read(GamePanel.class.getResourceAsStream("/org/soysal/defenders/Background3.png"));
  } 
  catch (IOException e) {}
  
  playButton.setText("PLAY AGAIN");
  playButton.setVisible(true);
 }
 
 @Override
 
 protected void paintComponent(Graphics g) //draws game elements such as player & enemies on the panel
 {
  super.paintComponent(g);
  setBackground(Color.BLACK);
  
  g.drawImage(background, 0, 0, this);
  
   if (gameActive) //checks if game is active, draws elements if so
   {  
    g.setColor(Color.GREEN);
    player.draw(g);
 
   g.setColor(Color.YELLOW);
    for(Bullet bullet: bullets)
     bullet.draw(g);
  
    g.setColor(Color.RED);
   for(Enemy enemy: enemies)
     enemy.draw(g);
     
    g.setColor(Color.BLUE);
    for (PowerUp power: powers)
     power.draw(g);

   }   
   else
   {
	    g.setColor(Color.WHITE);
	    g.drawString("Created by Ekin Soysal", 725, 525);
   }
   }
 
 @Override
 
 public boolean isFocusable() 
 {
  return true; 
 }
 
 
 
 public void keyPressed(KeyEvent ev) //sets controls, space bar to shoot, arrows to move left or right
 {
  //System.out.println(ev.getKeyCode());
 
  int code = ev.getKeyCode();
 switch(code) 
  {
   case 37: // left;
    player.moveLeft();
   revalidate();
   repaint();
   break;
 
   // case 38: // up;
   // break;
   case 39: // right;
   player.moveRight();
   revalidate();
   repaint();
   break;
 
   // case 40: // down;
   // break;
 
   case 32: // spacebar
   shoot();
   revalidate();
    repaint(); 
   break; 
  } 
 }
 
 
 
 public void keyReleased(KeyEvent ev) 
 {
  //System.out.println(ev.getKeyCode());
 }
 

 
 public void keyTyped(KeyEvent ev) {} 
  
 public void loadSound() //loads sound, two different sounds, shooting & explosions
 {
  try 
  {

   InputStream file = GamePanel.class.getResourceAsStream("/org/soysal/defenders/hit.wav");
   if (file!=null) 
   {
    shoot = AudioSystem.getClip();
    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
    shoot.open(ais);
   }
   
   InputStream file2 = GamePanel.class.getResourceAsStream("/org/soysal/defenders/explode.wav");
   if (file2!=null) 
   {
    explosion = AudioSystem.getClip();
    AudioInputStream ais = AudioSystem.getAudioInputStream(file2);
    explosion.open(ais);
   }   
   
  }
  catch (Exception e) {}
 }

 private void shoot() //player shooting, creates a new bullet and plays the shooting sound
  {
  if (player.getMissiles()==0)
   bullets.add(new Bullet(player.getX(), player.getY()-10));
  else
  { 
   bullets.add(new Missile(player.getX(), player.getY()-10));
   player.missileShot();
  }
  
  shoot.setFramePosition(0);
  shoot.start();  
 }
 
 private void dropPower(int a, int b) //creates a new power up where the enemy was destroyed
 {
  powers.add(new PowerUp(a, b));
 }
 
 private boolean enemyCheck(int x) //checks if there is an enemy on the place with given x coordinate and initial y coordinate where enemies spawn
 {
  for (Enemy enemy : enemies)
   if (x==enemy.getX())
    return true;
  
  return false;
 }
 
 private int getEnemyIndex() //returns a random x  coordinate where an enemy can spawn
 {
  int enemyIndex=(int)(Math.random()*(17.0));
  enemyIndex=(enemyIndex+1)*50;
  return enemyIndex;
 }
 
 private boolean dropChance() //sets the power up drop rate of enemies when destroyed
 {
  int powerChance=(int)(Math.random()*10);
  return powerChance==0;
 }
 

 public void actionPerformed(ActionEvent arg0) //checks for various functions in the game, such as enemies being shot or player being hit by enemies
 {
	 if (!gameActive)
		 return;
	 
  for (int i=0; i<bullets.size(); i++) //moves bullets, removes them if they are out of the panel
    if (!bullets.get(i).move(bulletSpeed))
   {
     bullets.remove(i);
     i--; 
   }
  revalidate();
  repaint(); 
   
  for (int i=0; i<powers.size(); i++) //moves power ups, removes them if they are out of the panel
   if (!powers.get(i).move(powerSpeed))
   {
    powers.remove(i);
    i--;
   }
   
  revalidate();
  repaint(); 
   
  for (int i=0; i<enemies.size(); i++) //moves enemies
   if (true)
   {
    enemies.get(i).move(enemySpeed);
   }
   
  revalidate();
  repaint();
  
  int newEnemyIndex;
  boolean enemyKilled=false;
  
  for (int b=0; b<bullets.size(); b++) //if an enemy is shot by player, harms or destroys enemy, if destroys, creates a new enemy and drops power up based on chance of dropping
   for (int e=0; e<enemies.size(); e++)
    if (enemies.get(e).getX()==bullets.get(b).getX() && (Math.abs(enemies.get(e).getY()+35-bullets.get(b).getY())<=30))
    {
     if (enemies.get(e).decreaseLives() || bullets.get(b).isMissile())
     {
      if (dropChance())
       dropPower(enemies.get(e).getX(), enemies.get(e).getY());
      enemies.remove(e);
      enemiesKilled++;
      enemyKilled=true;
      explosion.setFramePosition(0);
      explosion.start();
     }
     else
      enemyKilled=false;
     if (bullets.get(b).isMissile()==false)
      bullets.get(b).destroy();
     if (enemyKilled)
     {
      newEnemyIndex=getEnemyIndex();
      for (int n=0; n<enemies.size(); n++)
       if (enemies.get(n).getX()==newEnemyIndex && enemies.get(n).getY()<48)
        newEnemyIndex=getEnemyIndex();     
      if (enemiesKilled<10)
       enemies.add(new Enemy(newEnemyIndex));
      else if (enemiesKilled<20)
       enemies.add(new EnemyM(newEnemyIndex));
      else
       enemies.add(new EnemyH(newEnemyIndex));
     }
    }
    
  revalidate();
  repaint();
  
  for (int p=0; p<powers.size(); p++) //if player catches a power up, powers up player, giving him 10 missiles
   if (powers.get(p).getX()==player.getX() && (Math.abs(powers.get(p).getY()-player.getY())<=20))
   {
    player.powerUp();
    powers.remove(p);
   }
  
  revalidate();
  repaint();
  
  for (int b=0; b<bullets.size(); b++) //removes bullets out of panel
   if (bullets.get(b).getDestroyed())
    bullets.remove(b);
    
  revalidate();
  repaint();
  
  for (int e=0; e<enemies.size(); e++) //checks if enemies passed the player, displays end screen if so because player has failed
   if (enemies.get(e).getY()==600)
    gameEnd();
    
  revalidate();
  repaint();
  
  for (int e=0; e<enemies.size(); e++) //checks if an enemy hit player, displays end screen if so because player has failed
   if (enemies.get(e).getX()==player.getX() && enemies.get(e).getY()+25>=player.getY())
    gameEnd();
    
  revalidate();
  repaint();
 }
 
 }
 
class Player //class of the spaceship that player controls
{
 private int x;
  private int y;
  private int missilesLeft;
 
 public Player() //initializes player, sets x & y coordinates
 {
  x=450;
  y=500;
  missilesLeft=0; 
 }
 
 public void draw(Graphics g) //draws player
 {
  g.fillRect(x-2, y, 4, 25);
  g.fillRect(x-2, y+35, 4, 20); 
  g.fillRect(x-6, y+15, 4, 15);
  g.fillRect(x-10, y+25, 4, 25);
  g.fillRect(x-14, y+15, 4, 30);
  g.fillRect(x-18, y+40, 4, 10);
  g.fillRect(x-22, y+30, 4, 25); 
  g.fillRect(x-6, y+40, 4, 10); 
  g.fillRect(x+2, y+15, 4, 15);
  g.fillRect(x+6, y+25, 4, 25);
  g.fillRect(x+10, y+15, 4, 30);
  g.fillRect(x+14, y+40, 4, 10);
  g.fillRect(x+18, y+30, 4, 25);
  g.fillRect(x+2, y+40, 4, 10);
 }
 
 public int getX() 
 {
  return x;
 }
 
 public int getY() 
 {
  return y;
 }
 
 public void reset() //resets player's x coordinate for a new game, so that player can start in its initial position when a new game starts
 {
	 x=450;
	 missilesLeft=0;
 }
 
 public void moveRight() //moves player to right
 {
   if (x<850)
   x+=50;
 }
 
 public void moveLeft() //moves player to left
 {
  if (x>50)
   x-=50; 
 }
 
 public void destroy() //places player out of the panel
 {
  x+=1000;
  y+=1000;
 }
 
 public int getMissiles() //returns number of missiles player has
 {
  return missilesLeft;
 }
 
 public void powerUp() //powers up player, increasing missiles by 5
 {
  missilesLeft+=5;
 }
 
 public void missileShot() //decreases missiles by one, is used when player shoots a missile
 {
  missilesLeft-=1;
 }
}
 
class Bullet //class of bullet, which is the thing that the player shoots the enemies with
{ 
 private int x; 
 private int y;
 private boolean destroyed;
 
 public Bullet(int a, int b) //construct bullets related to players coordinates
  { 
  x=a; 
  y=b;
  destroyed=false;
 }
 
 public boolean move(int speed)  //moves bullet forward, checks if it's still on panel
 { 
  y-=speed;
   return (y>=0); 
 }
 
 public void draw(Graphics g) //draws bullet
  {   
  g.fillRect(x-1,y+6,2,12);
  g.fillRect(x-2,y+10,1,8);
  g.fillRect(x+1,y+10,1,8);
 }
 
 public int getX() 
 {
  return x;
 }
 
 public int getY() 
 {
  return y;
 }
 
 public boolean getDestroyed() //checks if bullet is destroyed by a collision with an enemy
 {
  return destroyed;
 }
 
 public void destroy() //moves bullet out of panel
 {
  y-=500;
  destroyed=true;
 }
 
 public boolean isMissile() //checks if bullet is a missile
 {
  return false;
 }
 
}
class Missile extends Bullet //a special type of bullet which does not get destroyed no matter how many collisions happen, given to player by power ups
{
 public Missile(int a, int b) //initializes missile
 {
  super(a,b);
 }
 
 public void draw(Graphics g) //draws missile
 {
  int x=getX();
  int y=getY();
  
  g.fillRect(x-2,y+6,4,12);
  g.fillRect(x-4,y+10,2,8);
  g.fillRect(x+2,y+10,2,8);
 }
 
 public boolean isMissile() //checks if bullet is a missile
 {
  return true;
 }
}
class Enemy //enemy spaceships which the player tries to destroy
{
 private int x;
 private int y;
 private int lives;
 
 public Enemy (int a) //initializes x and y coordinates of enemy, y coordinate is constant, sets enemy health, easy difficulty
 {
  x=a;
  y=4;
  lives=1;
 }
 
 public boolean move(int speed) //moves enemy downward
 {
  y+=speed;
  return (y>=0);
 }
 
 public void draw(Graphics g) //draws enemy
 {
  g.fillRect(x-15,y,6,4);
    g.fillRect(x-19,y+4,14,4);
    g.fillRect(x+9,y,6,4);
    g.fillRect(x+5,y+4,14,4);
    g.fillRect(x-24,y+8,48,4);
    g.fillRect(x-21,y+12,42,4);
    g.fillRect(x-17,y+16,34,4);
    g.fillRect(x-13,y+20,26,4);
    g.fillRect(x-9,y+24,18,4);
  g.fillRect(x-5,y+28,10,12);
  g.fillRect(x-2,y+36,4,10);
 }
 
 public int getX() 
 {
  return x;
 }
 
 public int getY() 
 {
  return y;
 }
 
 public int getLives() //gets how many bullets can enemy survive before exploding
 {
  return lives;
 }
 
 public void setLives(int n) //sets enemy lives, used at child classes
 {
  lives=n;
 }
 
 public boolean decreaseLives() //decreases enemy lives, used when enemy is shot by a bullet
 {
  lives--;
  return (lives<=0);
 }
}
class EnemyM extends Enemy //a stronger type of enemy, has two lives instead of one, medium difficulty
{ 
 public EnemyM(int a) //initializes enemy
 {
  super(a);
  setLives(2);
 }
 
 public void draw(Graphics g) //draws enemy
 {
  int x=getX();
  int y=getY();
  
  g.setColor(Color.MAGENTA);
  
  g.fillRect(x-2,y,4,48);
  g.fillRect(x-4,y,4,45);
  g.fillRect(x,y,4,45);
  g.fillRect(x+2,y,4,39);
  g.fillRect(x-6,y,4,39);
  g.fillRect(x-10,y+12,4,18);
  g.fillRect(x+6,y+12,4,18);
  g.fillRect(x-14,y+12,4,15);
  g.fillRect(x+10,y+12,4,15);
  g.fillRect(x-18,y,36,3);
  g.fillRect(x-14,y+3,28,6);
  g.fillRect(x-10,y+9,20,3);
  g.fillRect(x-18,y+15,4,9);
  g.fillRect(x+14,y+15,4,9);
 }
}
class EnemyH extends EnemyM //hardest type of enemy, has 3 lives
{
 public EnemyH(int a) //initializes enemy
 {
  super(a);
  setLives(3);
 }
 
 public void draw(Graphics g) //draws enemy
 {
  int x=getX();
  int y=getY();
  
  g.setColor(Color.decode("#ff8000"));
  
  g.fillRect(x-4,y+6,9,39);
  g.fillRect(x-7,y,3,45);
  g.fillRect(x+5,y,3,45);
  g.fillRect(x-10,y+3,3,39);
  g.fillRect(x+8,y+3,3,39);
  g.fillRect(x-13,y+6,3,27);
  g.fillRect(x+11,y+6,3,27);
  g.fillRect(x-16,y+12,3,18);
  g.fillRect(x+14,y+12,3,18);
  g.fillRect(x+17,y+16,3,18);
  g.fillRect(x-19,y+16,3,18);
 }
}
class PowerUp //special boosts that enemy can sometimes drop, gives player 5 missiles
{
 private int x;
 private int y;
 
 public PowerUp(int a, int b) //initializes x & y coordinates of power up
 {
  x=a;
  y=b;
 }
 
 public boolean move(int speed) //moves power up downwards
 { 
  y+=speed;
   return (y<=600); 
 }
 
 public void draw(Graphics g) //draws power up
 {
  g.fillRect(x-2,y+4,4,12);
  g.fillRect(x-4,y+8,2,8);
  g.fillRect(x+2,y+8,2,8);
  g.fillRect(x-4,y,8,2);
  g.fillRect(x-4,y+18,8,2);
  g.fillRect(x-6,y+2,2,2);
  g.fillRect(x-8,y+4,2,2);
  g.fillRect(x-6,y+16,2,2);
  g.fillRect(x-8,y+14,2,2);
  g.fillRect(x-10,y+6,2,8);
  g.fillRect(x+4,y+2,2,2);
  g.fillRect(x+6,y+4,2,2);
  g.fillRect(x+4,y+16,2,2);
  g.fillRect(x+6,y+14,2,2);
  g.fillRect(x+8,y+6,2,8);
 }
 
 public int getX() 
 {
  return x;
 }
 
 public int getY() 
 {
  return y;
 }
}
