
#include <SoftwareSerial.h>

// Instanciamos objeto SoftwareSerial para controlar el bluetooth

// Definición pines EnA y EnB para el control de la velocidad
int VelocidadMotor1 = 5; 
int VelocidadMotor2 = 6;

// Definición de los pines de control de giro de los motores In1, In2, In3 e In4
int Motor1A = 13; 
int Motor1B = 12;  
int Motor2C = 8; 
int Motor2D = 10;

// Definición de los pines de las luces y de la bocina
int LuzCarroFrontal1 = 4;
int LuzCarroFrontal2 = 9;
int Bocina = 7;

// Definición de la velocidad inicial de los motores
int speedMotor = 80;

// Variable para capturar el comando que llega desde la app
String cmd="";

void setup(){
  // Serial Monitor
  Serial.begin(9600);
  
  // Bluetooth Serial Port
  Serial.begin(9600);

  // Modo de pines de las luces
  pinMode(LuzCarroFrontal1,OUTPUT);
  pinMode(LuzCarroFrontal2,OUTPUT);

  // Modo del pin de la bocina
  pinMode(Bocina,OUTPUT); 

  // Establecemos modo de los pines del control de motores
  pinMode(Motor1A,OUTPUT);
  pinMode(Motor1B,OUTPUT);
  pinMode(Motor2C,OUTPUT);
  pinMode(Motor2D,OUTPUT);
  pinMode(VelocidadMotor1, OUTPUT);
  pinMode(VelocidadMotor2, OUTPUT);

  // Configuramos velocidad de los dos motores
  analogWrite(VelocidadMotor1, speedMotor); 
  analogWrite(VelocidadMotor2, speedMotor); 
}

void loop(){
  // Leemos los datos recibidos 

  while(Serial.available()>0){
    cmd+=(char)Serial.read();
    Serial.print(cmd);
  }

  // Programamos cada una de las acciones a realizar según el comando recibido
  if(cmd!=""){
    cmd = cmd[0];
    if(cmd == "S"){
      stopCar();
    }else
    if(cmd == "F"){
      moveForwardCar();
    }else
    if(cmd == "B"){
      moveBackwardsCar();
    }else
    if(cmd == "L"){
      turnLeftCar();
    }else
    if(cmd == "R"){
      turnRightCar();
    }
    if(cmd == "G"){
      moveForwardLeft();
    }else
    if(cmd == "I"){
      moveForwardRight();
    }else
    if(cmd == "H"){
      moveBackwardsLeft();
    }else
    if(cmd == "J"){
      moveBackwardsRight();
    }
    else
    if(cmd == "X"){
      digitalWrite(LuzCarroFrontal1, HIGH);
      digitalWrite(LuzCarroFrontal2, HIGH);
    }else
    if(cmd == "x"){
      digitalWrite(LuzCarroFrontal1, LOW);
      digitalWrite(LuzCarroFrontal2, LOW);
    }else
    if(cmd == "V"){
      digitalWrite(Bocina, HIGH);
    }else
    if(cmd == "v"){
      digitalWrite(Bocina, LOW);
    }else
    if(cmd == "0"){
      speedMotor = 80;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "1"){
      speedMotor = 90;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "2"){      
      speedMotor = 100;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "3"){
      speedMotor = 110;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "4"){
      speedMotor = 120;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "5"){
      speedMotor = 130;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "6"){
      speedMotor = 140;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "7"){
      speedMotor = 150;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "8"){
      speedMotor = 160;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "9"){
      speedMotor = 170;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }else
    if(cmd == "q"){
      speedMotor = 180;
      analogWrite(VelocidadMotor1, speedMotor); 
      analogWrite(VelocidadMotor2, speedMotor);
    }

    // Damos la orden como ejecutada y esperamos la siguiente
    cmd="";
    
  }if(Serial.available()<0) digitalWrite(Bocina, LOW); 
}

void stopCar(){
  // Paramos el carrito
  digitalWrite(Motor1A, LOW);
  digitalWrite(Motor1B, LOW);                       
  digitalWrite(Motor2C, LOW);
  digitalWrite(Motor2D, LOW); 
}

void turnRightCar(){
  // Configuramos sentido de giro para girar a la derecha
  digitalWrite(Motor1A, HIGH);
  digitalWrite(Motor1B, LOW);                       
  digitalWrite(Motor2C, LOW);
  digitalWrite(Motor2D, LOW); 
}

void turnLeftCar(){
  // Configuramos sentido de giro para girar a la izquierda
  digitalWrite(Motor1A, LOW);
  digitalWrite(Motor1B, LOW);                       
  digitalWrite(Motor2C, LOW);
  digitalWrite(Motor2D , HIGH); 
}

void moveForwardCar(){
  // Configuramos sentido de giro para avanzar
  digitalWrite(Motor1A, HIGH);
  digitalWrite(Motor1B, LOW);                       
  digitalWrite(Motor2C, LOW);
  digitalWrite(Motor2D, HIGH); 
}

void moveBackwardsCar(){
  // Configuramos sentido de giro para retroceder
  digitalWrite(Motor1A, LOW);
  digitalWrite(Motor1B, HIGH);                       
  digitalWrite(Motor2C, HIGH);
  digitalWrite(Motor2D, LOW); 
}

void moveForwardLeft(){  
  // Giramos a la izquierda mientras avanza
  analogWrite(VelocidadMotor2, speedMotor + 60);
  
  digitalWrite(Motor1A, HIGH);
  digitalWrite(Motor1B, LOW);                       
  digitalWrite(Motor2C, LOW);
  digitalWrite(Motor2D, HIGH); 

  delay(20);
  analogWrite(VelocidadMotor2, speedMotor);
}

void moveForwardRight(){
  // Giramos a la derecha mientras avanza
  analogWrite(VelocidadMotor1, speedMotor + 60);
    
  digitalWrite(Motor1A, HIGH);
  digitalWrite(Motor1B, LOW);                       
  digitalWrite(Motor2C, LOW);
  digitalWrite(Motor2D, HIGH); 
  
  delay(20);
  analogWrite(VelocidadMotor1, speedMotor);
}

void moveBackwardsLeft(){
  // Giramos a la izquierda mientras retrocede
  analogWrite(VelocidadMotor2, speedMotor + 60);
    
  digitalWrite(Motor1A, LOW);
  digitalWrite(Motor1B, HIGH);                       
  digitalWrite(Motor2C, HIGH);
  digitalWrite(Motor2D, LOW); 

  delay(20);
  analogWrite(VelocidadMotor2, speedMotor);
}

void moveBackwardsRight(){
  // Giramos a la derecha mientras retrocede
  analogWrite(VelocidadMotor1, speedMotor + 60);
    
  digitalWrite(Motor1A, LOW);
  digitalWrite(Motor1B, HIGH);                       
  digitalWrite(Motor2C, HIGH);
  digitalWrite(Motor2D, LOW); 

  delay(20);
  analogWrite(VelocidadMotor1, speedMotor);
}
