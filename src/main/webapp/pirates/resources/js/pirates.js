var updateIslandArray = function(){

    var ia = [];
    //var text;

    for(var i = 0; i < window.gameData.map.length; i++)
    {
        //text = "";
        for(var j = 0; j < window.gameData.map[i].length; j++)
        {
            //text += gameData.map[i][j] + "";
            
            if(window.gameData.map[i][j] == "L")
            {
                ia.push({posX: j, posY: i});
            }                        
        }
        
    }
    //console.log(text);
    //islandArray = ia;
    //console.log("island array length: " + ia.length);
    //console.log("island array contents: " + JSON.stringify(ia));

    window.gameData.islandArray =  ia;
}

var gameState = {
    playerStates : new Map(),
    cannonBallStates : []
}

//Test players;
var playerStates = function(){
    var playerMap = new Map();
    playerMap.set(1, new PlayerState(10,0,100,90,{x:0,y:0}, "RED", "player1", 1));
    playerMap.set(2, new PlayerState(20,20,100,90,{x:0,y:0}, "BLUE", "player2", 2));
    playerMap.set(3, new PlayerState(10,10,100,90,{x:0,y:0}, "GREEN", "player3", 3));
    playerMap.set(3, new PlayerState(100,100,100,90,{x:0,y:0}, "GREEN", "player3", 3));
    return playerMap;
}

var gameWidth = 800;
var gameHeight = 600;

var game = new Phaser.Game(window.gameWidth, window.gameHeight, Phaser.AUTO, 'pirates!', { preload: preload, create: create, update: update, render: render });

function preload () {

    game.load.image('ship', 'resources/assets/shipblack.png');
    game.load.image('enemy', 'resources/assets/shipred.png');
    game.load.image('cannonBall', 'resources/assets/cannonball.png');
    game.load.image('sea', 'resources/assets/sea.png');
    game.load.image('land', 'resources/assets/land.png');
    game.load.image('sand', 'resources/assets/sand.png');
    game.load.spritesheet('kaboom', 'resources/assets/explosion.png', 64, 64, 23);
    game.load.image('wake', 'resources/assets/wake.png');
    game.load.image('cannon', 'resources/assets/cannon.png');
    
}

var sea;
var islands;

var isStateChanged = false;
var playerState;
var wake;
var ship;
var cannonL;
var cannonR;
var cannonBallStates = [];

var enemies;
var enemyCannonBalls;
var enemiesTotal = 0;
var enemiesAlive = 0;
var explosions;

var rudder = 0;
var deltaRudder = 0.025
var maxRudder = 1;
var currentSpeed = 0;
var maxSpeed = 150;
var cursors;
var wasd;
var keys;
var timeLastDown;

var stuckTimeLimit = 3000; //in milliseconds
var islandTimer = 0;
var messageDelay = 50; //in milliseconds
var messageTimer = 0;

var cannonBalls;
var fireRate = 1000;
var nextFireL = 0;
var nextFireR = 0;

var maxHealth = 100;
var health = 100;
var barconfig;
var healthbar;

function create () {


    //  Resize our game world to be a 2000 x 2000 square
    var worldW = window.gameData.worldW();
    var worldH = window.gameData.worldH();
    //console.log("(worldW,worldH) -> (" + worldW + "," + worldH + ")")
    var minBoundX = -(Math.floor(worldW/2));
    var minBoundY = -(Math.floor(worldH/2));
    var islandArray = window.gameData.islandArray;
    window.game.world.setBounds(minBoundX, minBoundY, worldW, worldH);

    //  Tiled background world
    window.sea = window.game.add.tileSprite(minBoundX, minBoundY, worldW, worldH, 'sea');
    window.sea.fixedToCamera = false;

    window.islands = [];
    window.trees = [];
    //game.add.group();

    window.updateIslandArray();
    //gameState.playerStates = playerStates();


    //console.log("playerStates type: " + typeof gameState.playerStates)
    //Load the islands from the data:
    var islandText = "";
    for(var i = 0; i < window.gameData.islandArray.length; i++)
    {
        //console.log("console log 2 reached:");
        islandText += "(" + window.gameData.islandArray[i].posX + "," + window.gameData.islandArray[i].posY + ") ";
        window.islands.push(game.add.sprite((window.gameData.islandArray[i].posX * window.gameData.tileSize + minBoundX), (window.gameData.islandArray[i].posY * window.gameData.tileSize + minBoundY) , 'sand'));
        window.islands[i].visible = true;
        window.islands[i].bringToTop();
        window.game.physics.enable(window.islands[i], Phaser.Physics.ARCADE);  
    }

    console.log("islands @: " + islandText);
    //console.log("console log 3 reached: " + JSON.stringify(gameData.map));

    

    //  The base of our ship
    window.ship = game.add.sprite(window.game.world.randomX, window.game.world.randomY, 'ship');
    window.ship.anchor.setTo(0.5, 0.5);

    //  This will force it to decelerate and limit its speed
    window.game.physics.enable(window.ship, Phaser.Physics.ARCADE);
    window.ship.body.drag.set(0.2);
    window.ship.body.maxVelocity.setTo(400, 400);
    window.ship.body.collideWorldBounds = true;

    //  Finally the cannons that we place on-top of the ship body
    window.cannonL = window.game.add.sprite(0, 0, 'cannon'); 
    window.cannonL.anchor.set(0, 0.5);  
    window.cannonR = window.game.add.sprite(0, 0, 'cannon');    
    window.cannonR.anchor.set(0, 0.5);

    //  The enemies cannonBall group
    window.enemyCannonBalls = window.game.add.group();
    window.enemyCannonBalls.enableBody = true;
    window.enemyCannonBalls.physicsBodyType = Phaser.Physics.ARCADE;
    window.enemyCannonBalls.createMultiple(100, 'cannonBall');
    
    window.enemyCannonBalls.setAll('anchor.x', 0.5);
    window.enemyCannonBalls.setAll('anchor.y', 0.5);
    window.enemyCannonBalls.setAll('outOfBoundsKill', true);
    window.enemyCannonBalls.setAll('checkWorldBounds', true);

    //  Create some baddies to waste :)
    window.enemies = new Map();

    window.enemiesTotal = 0;
    window.enemiesAlive = 0;
    /*
    for(var [id, playerState] of gameState.playerStates) {
        enemies.set(new EnemyShip(playerState.posX, playerState.posY, id, playerState.health, game, ship, enemyCannonBalls));
        enemiesTotal++;
        enemiesAlive++;
    }; */

    //  A shadow below our ship
    window.wake = window.game.add.sprite(0, 0,'wake');
    window.wake.anchor.setTo(0.7, 0.5);

    //  Our cannonBall group
    window.cannonBalls = window.game.add.group();
    window.cannonBalls.enableBody = true;
    window.cannonBalls.physicsBodyType = Phaser.Physics.ARCADE;
    window.cannonBalls.createMultiple(30, 'cannonBall', 0, false);
    window.cannonBalls.setAll('anchor.x', 0.5);
    window.cannonBalls.setAll('anchor.y', 0.5);
    window.cannonBalls.setAll('outOfBoundsKill', true);
    window.cannonBalls.setAll('checkWorldBounds', true);

    window.ship.bringToTop();
    window.cannonL.bringToTop();
    window.cannonR.bringToTop();

    //  Explosion pool
    window.explosions = game.add.group();

    for (var i = 0; i < 25; i++)
    {
        var explosionAnimation = window.explosions.create(0, 0, 'kaboom', [0], false);
        explosionAnimation.anchor.setTo(0.5, 0.5);
        explosionAnimation.animations.add('kaboom');
    }

    window.game.world.bringToTop(explosions);

    window.game.camera.follow(window.ship, Phaser.Camera.FOLLOW_LOCKON);
    //game.camera.deadzone = new Phaser.Rectangle(150, 150, 500, 300);
    //game.camera.focusOnXY(0, 0);

    window.cursors = window.game.input.keyboard.createCursorKeys();
    window.keys = {
        up : game.input.keyboard.addKey(Phaser.Keyboard.W),
        down: game.input.keyboard.addKey(Phaser.Keyboard.S),
        left: game.input.keyboard.addKey(Phaser.Keyboard.A),
        right: game.input.keyboard.addKey(Phaser.Keyboard.D),
        fireL: game.input.keyboard.addKey(Phaser.Keyboard.SHIFT),
        fireR: game.input.keyboard.addKey(Phaser.Keyboard.SPACEBAR),
        faster: game.input.keyboard.addKey(Phaser.Keyboard.R),
        slower: game.input.keyboard.addKey(Phaser.Keyboard.F),
        anchor: game.input.keyboard.addKey(Phaser.Keyboard.X)
    }

    window.barconfig = {
        //width: gameWidth - gameWidth/10,
        width: gameWidth,
        height: 10,
        x: gameWidth/2,
        y: gameHeight-5,
        //x: -gameWidth/2 + gameWidth/20,
        //y: gameHeight/2 - gameHeight/20,
        bg: {
          color: '#381828'
        },
        bar: {
          color: '#CE2203'
        },
        animationDuration: 200,
        flipped: false
    };
    window.healthbar = new HealthBar(window.game, window.barconfig);
    window.healthbar.setFixedToCamera(true);
    window.healthbar.setPercent(100);
    window.game.world.bringToTop(healthbar);

    window.playerState = new PlayerState(window.ship.x, window.ship.y, window.health, window.ship.rotation, window.ship.body.velocity, "BLACK", "player" + window.gameData.socketId, window.gameData.socketId);
    console.log("create playerState: " + JSON.stringify(window.playerState));
    window.isStateChanged = false;
}
//End Create

function update () {

    //isStateChanged = false;

    //create new cannonballs from websocket data queue
    if(window.gameState.cannonBallStates.length > 0)
    {
        var cs;
        var cannonBall;

        for(var i = 0; i < window.gameState.cannonBallStates.length; i++)
        {
            cs = window.gameState.cannonBallStates[i];
            //console.log("cannonBallState: " + JSON.stringify(cs));
            
            cannonBall = window.enemyCannonBalls.getFirstDead();
            cannonBall.reset(cs.posX, cs.posY); 
            cannonBall.body.velocity.x = cs.velX;
            cannonBall.body.velocity.y = cs.velY;
        }
        window.gameState.cannonBallStates = [];
    }

    window.game.physics.arcade.overlap(window.enemyCannonBalls, window.ship, window.cannonBallHitPlayer, null, this);

    window.enemiesAlive = 0; 

    var idSet = new Set();

    //Update local enemies from playerStates data
    for(var [id, playerState] of window.gameState.playerStates) {
        
        //console.log("incoming enemyState: " + JSON.stringify(playerState));

        idSet.add(id);

        if(!window.enemies.has(id))//create local enemy and inject playerState data
        {
            window.enemies.set(id, new EnemyShip(playerState.posX, playerState.posY, id, playerState.health, window.game, window.ship, window.enemyCannonBalls));
            window.enemiesTotal++;
        }  

        //update local enemy from playerState data
        if(window.enemies.has(id))
        {
           if(window.enemies.get(id).alive){
                window.enemiesAlive++;              

                window.game.physics.arcade.collide(window.ship, window.enemies.get(id).ship);
                window.enemies.get(id).update();
                window.game.physics.arcade.overlap(window.cannonBalls, window.enemies.get(id).ship, window.cannonBallHitEnemy, null, this);                
           }
        }
              
    };  

    //Delete local enemies that are no longer tied to playerStates
    for (var [id, enemyState] of window.enemies){
        if(!idSet.has(id))
        {
            window.enemiesTotal--;
            window.enemiesAlive--;
            window.enemies.delete(id);
        }
    }

    //Player-island collission:
    for(var i = 0; i < window.islands.length; i++)
    {
        //game.physics.arcade.overlap(islands[i], enemies.get(id).ship);
        //Player collision with an island:
        window.game.physics.arcade.overlap(window.ship, window.islands[i], window.playerHitIsland, null, this);
    }    

    //Reset island collision timeout
    if(window.islandTimer != 0 && window.game.time.now > window.islandTimer)
    {
        window.islandTimer = 0;
    }

    if(window.keys.anchor.isDown)
    {
        window.rudder = 0;
        //currentSpeed = 0;
        //ship.body.velocity.x = 0;
        //ship.body.velocity.y = 0;
        //isStateChanged = true;
    }

    if (window.keys.left.isDown)
    {
        if(window.rudder >= -window.maxRudder)
            window.rudder -= window.deltaRudder;

        window.isStateChanged = true;
    }
    else if (window.keys.right.isDown)
    {
        if(window.rudder <= window.maxRudder)
            window.rudder += window.deltaRudder;
        
        window.isStateChanged = true;
    }

    if(window.rudder != 0)
    {
        window.ship.angle += window.rudder;
        window.isStateChanged = true;
    }

    if (window.keys.up.isDown)
    {
        //  The speed we'll travel at
        if(window.currentSpeed < window.maxSpeed)
            window.currentSpeed += window.maxSpeed/100;
        if(window.wake.alpha < window.currentSpeed/window.maxSpeed)
            window.wake.alpha += 0.025;
        window.isStateChanged = true;
    }
    else
    {
        //if (currentSpeed > 0)
        //{
        //    currentSpeed -= 10;
       // }        
        //wake.alpha = currentSpeed/maxSpeed;
    }

    if (window.keys.down.isDown)
    {
        if(window.currentSpeed > 0)
        {
            window.currentSpeed -= window.maxSpeed/100;
            if(window.wake.alpha > 0)
                window.wake.alpha -= 0.025;
        }
        window.isStateChanged = true;
    }

    if (window.currentSpeed > 0)
    {
        window.game.physics.arcade.velocityFromRotation(window.ship.rotation, window.currentSpeed, window.ship.body.velocity);
        window.isStateChanged = true;
    }

    //sea.tilePosition.x = -game.camera.x;
    //sea.tilePosition.y = -game.camera.y;

    //  Position all the parts and align rotations
    window.wake.x = window.ship.x;
    window.wake.y = window.ship.y;
    window.wake.rotation = window.ship.rotation;

    window.cannonL.x = window.ship.x;
    window.cannonL.y = window.ship.y;
    window.cannonR.x = window.ship.x;
    window.cannonR.y = window.ship.y;

    window.cannonL.rotation = window.ship.rotation - 0.5 * Math.PI; //game.physics.arcade.angleToPointer(cannon);
    window.cannonR.rotation = window.ship.rotation + 0.5 * Math.PI;

    if (window.keys.fireL.isDown)
    {
        //  Boom!
        window.fireL();
        window.isStateChanged = true;
    }

    //game.input.activePointer.isDown
    if (window.keys.fireR.isDown)
    {
        //  Boom!
        window.fireR();
        window.isStateChanged = true;
    }

    //playerState = new PlayerState(ship.x, ship.y, health, ship.rotation, ship.body.velocity, "BLACK", "player" + gameData.socketId, gameData.socketId);
    if(window.game.time.now > window.messageTimer)
    {
        //console.log("playerState: " + JSON.stringify(window.playerState));
        window.playerState.posX = window.ship.x;
        window.playerState.posY = window.ship.y;
        window.playerState.health = window.health;
        window.playerState.rotation = window.ship.rotation;
        window.playerState.velocity = window.ship.body.velocity;
        window.cannonBallStates.push(window.playerState);
        window.webSocket.send(JSON.stringify(window.cannonBallStates));
        //console.log("PlayerState message sent: " + JSON.stringify(window.cannonBallStates));
        window.cannonBallStates = [];
        window.isStateChanged = false;
        window.messageTimer = window.game.time.now + window.messageDelay;
    }
}
//End update

function playerHitIsland (ship, island) {
    var tempX = ship.body.velocity.x;
    var tempY = ship.body.velocity.y;
    var angleOfAttack = game.math.angleBetween(ship.x, ship.y, island.x, island.y);
   ship.rotation = (ship.rotation + 2 * angleOfAttack) % (2 * Math.PI);
    //ship.angle = ((ship.angle - 2 * 360 * angleOfAttack / (2 * Math.PI)) + 180) % 360;
    if(window.islandTimer == 0)
    {
        window.islandTimer = window.game.time.now + window.stuckTimeLimit;
    }

    if(window.game.time.now >= window.islandTimer)
    {
        ship.x = window.game.world.randomX;
        ship.y = window.game.world.randomY;
        window.islandTimer = 0;
    }
}

function cannonBallHitPlayer (ship, cannonBall) {

    var explosionAnimation2 = window.explosions.getFirstExists(false);
        explosionAnimation2.reset(cannonBall.x, cannonBall.y);
        explosionAnimation2.play('kaboom', 30, false, true);

    cannonBall.kill();
    damage(10);

}

function damage (n){
    window.health -= n;
    
    if (window.health < 0)
    {
        window.health = window.maxHealth;

        window.ship.x = window.game.world.randomX;
        window.ship.y = window.game.world.randomY;

        var explosionAnimation2 = window.explosions.getFirstExists(false);
        //game.world.bringToTop(explosionAnimation2);
        explosionAnimation2.reset(window.ship.x, window.ship.y);
        explosionAnimation2.play('kaboom', 30, false, true);
    }

    window.healthbar.setPercent((window.health/window.maxHealth) * 100);
}

function cannonBallHitEnemy (ship, cannonBall) {

    var explosionAnimation2 = window.explosions.getFirstExists(false);
        explosionAnimation2.reset(cannonBall.x, cannonBall.y);
        explosionAnimation2.play('kaboom', 30, false, true);

    cannonBall.kill();

    var destroyed = window.enemies.get(ship.id).damage(10);

    if (destroyed)
    {
        var explosionAnimation = window.explosions.getFirstExists(false);
        explosionAnimation.reset(ship.x, ship.y);
        explosionAnimation.play('kaboom', 30, false, true);
    }

}

function fireL () {

    if (window.game.time.now > window.nextFireL && window.cannonBalls.countDead() > 0)
    {
        window.nextFireL = window.game.time.now + window.fireRate;

        var cannonBall = window.cannonBalls.getFirstExists(false);        

        cannonBall.reset(window.cannonL.x, window.cannonL.y);

        cannonBall.rotation = window.cannonL.rotation;
        //cannonBall

        window.game.physics.arcade.velocityFromRotation(cannonBall.rotation, 1000, cannonBall.body.velocity);
        cannonBall.body.velocity.x += window.ship.body.velocity.x;
        cannonBall.body.velocity.y += window.ship.body.velocity.y;

        window.cannonBallStates.push(new CannonBallState(cannonBall.x, cannonBall.y, cannonBall.body.velocity, window.gameData.socketId));

        //var explosionAnimation = explosions.getFirstExists(false);
        //explosionAnimation.bringToTop();
        //explosionAnimation.reset(cannonL.x, cannonL.y);
        //explosionAnimation.play('kaboom', 30, false, true);
        //cannonBall.rotation = game.physics.arcade.moveToPointer(cannonBall, 1000, game.input.activePointer, 500);        
    }
}

function fireR () {

    if (window.game.time.now > window.nextFireR && window.cannonBalls.countDead() > 0)
    {
        window.nextFireR = window.game.time.now + window.fireRate;

        var cannonBall = window.cannonBalls.getFirstExists(false);

        cannonBall.reset(window.cannonR.x, window.cannonR.y);
 
        cannonBall.rotation = window.cannonR.rotation;

        //cannonBall.angle = cannonR.angle;
        window.game.physics.arcade.velocityFromRotation(cannonBall.rotation, 1000, cannonBall.body.velocity);
        cannonBall.body.velocity.x += window.ship.body.velocity.x;
        cannonBall.body.velocity.y += window.ship.body.velocity.y;

        window.cannonBallStates.push(new CannonBallState(cannonBall.x, cannonBall.y, cannonBall.body.velocity, window.gameData.socketId));

        //var explosionAnimation = explosions.getFirstExists(false);
        //explosionAnimation.bringToTop();
        //explosionAnimation.reset(cannonR.x, cannonR.y);
        //explosionAnimation.play('kaboom', 30, false, true);
        //cannonBall.rotation = game.physics.arcade.moveToPointer(cannonBall, 1000, game.input.activePointer, 500);        
    }
}

function render () {

    // game.debug.text('Active Bullets: ' + cannonBalls.countLiving() + ' / ' + cannonBalls.length, 32, 32);
    window.game.debug.text('Enemies: ' + window.enemiesAlive, 32, 32); // + ' / ' + window.enemiesTotal, 32, 32);

}

//EnemyShip(playerState.posX, playerState.posY, id, playerState.health, game, ship, enemyCannonBalls));
EnemyShip = function (x, y, id, health, game, player, cannonBalls) {

    //var x = game.world.randomX;
    //var y = game.world.randomY;
    
    this.prevX = x;
    this.prevY = y;

    this.game = game;
    this.health = health;
    this.maxHealth = health;
    this.player = player;
    this.cannonBalls = cannonBalls;
    this.fireRate = 2000; //in milliseconds
    this.nextFireL = 0;
    this.nextFireR = 500;
    this.alive = true;
    this.id = id;
    this.updateTimer = 0;

    this.wake = game.add.sprite(x, y, 'wake');
    this.ship = game.add.sprite(x, y, 'enemy');
    this.cannonL = game.add.sprite(x, y, 'cannon');
    this.cannonR = game.add.sprite(x, y, 'cannon');

    this.wake.anchor.set(0.7, 0.5);
    this.ship.anchor.set(0.5);
    this.cannonL.anchor.set(0, 0.5);
    this.cannonR.anchor.set(0, 0.5);
    
    this.ship.id = id;
    this.ship.name = id.toString();
    this.game.physics.enable(this.ship, Phaser.Physics.ARCADE);
    this.ship.body.immovable = false;
    this.ship.body.collideWorldBounds = true;
    this.ship.body.bounce.setTo(1, 1);

    this.ship.angle = game.rnd.angle();

    this.barconfig = {
        width: 80,
        height: 5,
        x: x,
        y: y,
        bg: {
          color: '#381828'
        },
        bar: {
          color: '#CE2203'
        },
        animationDuration: 200,
        flipped: false
    };
    this.healthbar = new HealthBar(this.game, this.barconfig);

    game.physics.arcade.velocityFromRotation(this.ship.rotation, 100, this.ship.body.velocity);

};

EnemyShip.prototype.damage = function(n) {

    this.health -= n;
    this.healthbar.setPercent((this.health/this.maxHealth) * 100);

    if (this.health <= 0)
    {
        this.alive = false;

        this.wake.kill();
        this.ship.kill();
        this.cannonL.kill();
        this.cannonR.kill();
        this.healthbar.kill();

        enemies.delete(this.id);

        return true;
    }

    return false;

}

EnemyShip.prototype.update = function() {

    this.healthbar.setPosition(this.ship.x, this.ship.y - 50);

    if(this.prevX != this.ship.x && this.prevY != this.ship.y)
        this.ship.rotation = this.game.math.angleBetween(this.prevX, this.prevY, this.ship.x, this.ship.y);   

    //Update local enemy with playerState data
    if(window.gameState.playerStates.has(this.id) && this.game.time.now > this.updateTimer)
    {
        var ps = window.gameState.playerStates.get(this.id);
        this.ship.x = ps.posX;
        this.ship.y = ps.posY;

        this.ship.body.velocity.x = ps.velocity.x;
        this.ship.body.velocity.y = ps.velocity.y;

        this.ship.health = ps.health;
        this.ship.rotation = ps.rotation;

        this.updateTimer = this.game.time.now + window.messageDelay;
    }

    this.wake.x = this.ship.x;
    this.wake.y = this.ship.y;
    this.wake.rotation = this.ship.rotation;

    if(this.wake.alpha < this.ship.body.speed/window.maxSpeed)
        this.wake.alpha += 0.025;
    else    this.wake.alpha = this.ship.body.speed / window.maxSpeed;

    if(this.ship.body.speed == 0)
        this.wake.alpha = 0;
    
     

    this.cannonL.x = this.ship.x;
    this.cannonL.y = this.ship.y;
    this.cannonL.rotation = this.ship.rotation - 0.5 * Math.PI; //this.game.physics.arcade.angleBetween(this.ship, this.player);

    this.cannonR.x = this.ship.x;
    this.cannonR.y = this.ship.y;
    this.cannonR.rotation = this.ship.rotation + 0.5 * Math.PI; //this.game.physics.arcade.angleBetween(this.ship, this.player);

//AI cannonball firing:
    /* 
    if (this.game.physics.arcade.distanceBetween(this.ship, this.player) < 300)
    {
        if (this.game.time.now > this.nextFireL && this.cannonBalls.countDead() > 0)
        {
            this.nextFireL = this.game.time.now + this.fireRate;

            var cannonBall = this.cannonBalls.getFirstDead();

            cannonBall.reset(this.cannonL.x, this.cannonL.y);            

            cannonBall.rotation = this.game.physics.arcade.moveToObject(cannonBall, this.player, 500);

            //var explosionAnimation = explosions.getFirstExists(false);
            //explosionAnimation.bringToTop();
            //explosionAnimation.reset(this.cannonL.x, this.cannonL.y);
            //explosionAnimation.play('kaboom', 30, false, true);
        }
        if (this.game.time.now > this.nextFireR && this.cannonBalls.countDead() > 0)
        {
            this.nextFireR = this.game.time.now + this.fireRate;

            var cannonBall = this.cannonBalls.getFirstDead();            

            cannonBall.reset(this.cannonR.x, this.cannonR.y);

            cannonBall.rotation = this.game.physics.arcade.moveToObject(cannonBall, this.player, 500);

            //var explosionAnimation2 = explosions.getFirstExists(false);
            //explosionAnimation2.bringToTop();
            //explosionAnimation2.reset(this.cannonR.x, this.cannonR.y);
            //explosionAnimation2.play('kaboom', 30, false, true);
        }
    }
    */
    
    this.prevX = this.ship.x;
    this.prevY = this.ship.y;
};

/*//Test gameData
var gameData = {
    tilesX : 16,
    tilesY : 16,
    tileSize : 128,
    map :       [["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","L","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","L","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","L","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","L","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","L","L","L","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"],
                ["~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"]],
     
    worldW : function() {
        return this.tileSize * this.tilesX;
    },

    worldH : function() {
        return this.tileSize * this.tilesY;
    },

    //islandArray = [],

    //TODO: check what type of island tile needs to be pushed
    islandArray : []
    //TODO: palmtrees!
}
*/

