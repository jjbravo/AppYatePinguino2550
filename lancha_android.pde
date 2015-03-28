//char* strin="Bienvenido\n";
int a=0,state;
u8 dato=0;
int tempc=0;

void setup(){
pinMode(0,OUTPUT);
digitalWrite(0,LOW);

Serial.begin(9600);

servo.attach(0);
}
void loop(){


	while(Serial.available()){

		
			
		 dato=Serial.read();
		 Serial.printf("%d",dato);
		 
		// Serial.printf("%f",5.0);
		 
	if(dato==97){
		state=1;
	}else if(dato==98){
		state=0;
		analogWrite(11,0);
		delay(1);
		servo.write(0,130);
		delay(1);
	}
	
if(state==1){
	
	 switch(dato){
			case 108:
				analogWrite(11,1023); //a
				delay(1);
				
				break;
			case 107:
				analogWrite(11,910); //b
				delay(1);
				break;
			case 106:
				analogWrite(11,797); //c
				delay(1);
				break;
			case 105:
				analogWrite(11,684); //d
				delay(1);
				break;
			case 104:
				analogWrite(11,571); //f
				delay(1);
				break;
			case 103:
				analogWrite(11,458); //t
				delay(1);
				break;
			case 102:
				analogWrite(11,345); //y
				delay(1);
				break;
			case 101:
				analogWrite(11,232); //u
				delay(1);
				break;
			case 100:
				analogWrite(11,119); //i
				delay(1);
				break;
			case 99:
				analogWrite(11,0); //o
				delay(1);
				break;
	 //***************************
			case 109:
				servo.write(0,25);
				delay(1);
				
				break;
			case 110:
				servo.write(0,50);
				delay(1);
				break;
			case 111:
				servo.write(0,75);
				delay(1);
				break;
			case 112:
				servo.write(0,100);
				delay(1);
				break;
			case 113:
				servo.write(0,125);
				delay(1);
				break;
				
			case 114:
				servo.write(0,150);
				delay(1);
				break;
			case 115:
				servo.write(0,175);
				delay(1);
				break;
			case 116:
				servo.write(0,200);
				delay(1);
				break;
			case 117:
				servo.write(0,225);
				delay(1);
				break;
			case 118:
				servo.write(0,250);
				delay(1);
				break;
		}
	 
	  }
	  
	  
	
	
	} //llave del while 
		
		
	
}