package com.example.appyatepinguino2550;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import com.example.appyatepinguino2550.ConexionBT;
import com.example.appyatepinguino2550.R;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AppYateActivity extends ActionBarActivity implements SensorEventListener, OnClickListener {
	
	float curX=0;
	private int curY=0, curZ=0;
//	TextView acX,acY, acZ;
	ImageView  on_motor;
	ImageView aceler,timon;
	
	int state_on_motor=0;
	private Set<BluetoothDevice>pairedDevices;
	private int dato_read; 
	
	public static final String TAG ="LanchaAndroid";
	public static final boolean D=true;
	//Tipos de mensajes enviados y recibidos dsde el handler de conexion
	public static final int Mensaje_Estado_Cambiado =1;
	public static final int Mensaje_Leido=2;
	public static final int Mensaje_Escrito=3;
	public static final int Mensaje_Nombre_Dispositivo=4;
	public static final int Mensaje_TOAST=5;
	public static final int MESSAGE_Desconectado=6;
	public static final int REQUEST_ENABLE_TB=7;
	public static String DEVICE_NAME="device_name";
	public static final String TOAST = "toast";
	private boolean seleccionador = false;
	public static   String stateConnect="0";
	private Vibrator vibrador;
	//public static final String address="20:13:08:09:05:27";
	//nombre del dispositivo
	private String mConnectedDeviceName=null;
	//adaptador bluetooth local
	private BluetoothAdapter AdaptadorBT=null;
	//objeto miembro para el servicio de ConexionBT
	private ConexionBT Servicio_BT = null;
		  // Insert your server's MAC address
	private static String MAC = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_yate);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 aceler=(ImageView) findViewById(R.id.x0);
	        timon=(ImageView) findViewById(R.id.y0);
			
			on_motor=(ImageView) findViewById(R.id.on_motor);
			
			
			on_motor.setOnClickListener(this);
			
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_yate, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		 switch (item.getItemId()) {
		 	
		 
	        case R.id.idconectar:
	          
	        	if(stateConnect=="1"){
					if(Servicio_BT != null) 
					
					item.setTitle("Conectar");
					Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();
					Servicio_BT.stop();	
					
					stateConnect ="0";
				}else{
				pairedDevices= AdaptadorBT.getBondedDevices();	
				//pairedDevices=BA.getBondedDevices();
				ArrayList list = new ArrayList();
				for(BluetoothDevice bt : pairedDevices)
					list.add(bt.getName()+"\n"+bt.getAddress());
				Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();
				final ArrayAdapter adapter = new ArrayAdapter
						(this,android.R.layout.simple_list_item_single_choice,list);
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("Conectar con:").setSingleChoiceItems(adapter,-1, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						String[] dato=null;
						String mac=null;
		          
		                	mac=(String) adapter.getItem(arg1);
		                	dato=mac.split("\n");
		                
		                		MAC=dato[1];
		                		DEVICE_NAME=dato[0];
		              
					}
				}).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int id){
						
					connect(MAC);
					
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
	        	
	        	item.setTitle("Desconectar");
	             builder.show();
				}
	        	
	            return true;
		 
		case R.id.idayuda:
			AlertDialog.Builder builders = new AlertDialog.Builder(this);
	builders.setTitle("Ayuda");
	builders.setMessage("Comandos de envio:\n" +
			"Al presionar On envia  'a' en ASCII = 97\n" +
			"Al presionar Off envia 'b' en ASCII = 98\n" +
			"El resto de comandos enviados de acuerdo a la " +
			"posicion del acelerometro, son desde la letra 'c' hasta la letra 'v',\n" +
			"de las cuales, desde la 'c hasta la 'l' son para la aceleracion de motor" +
			"y desde la 'm' hasta la 'v' son lara el timon\n");
	builders.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
	                //Toast.makeText(getApplication(), "Yes", Toast.LENGTH_SHORT).show();
				}
			});
	
    builders.show();
			 return true;
		case R.id.idacercade:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Acerca de App Yate Pinguino 2550");
	builder.setMessage("App Yate Pinguino 2550 es una aplicacion basica para uso con la board Pinguino 2550 \nLa aplicacion esta creada con " +
			"fines educativos por Jhon Jairo Bravo C. bajo los terminos de Licencia Open Source GNU\n" +
			"bramasterweb@gmail.com\n");
	builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
	                //Toast.makeText(getApplication(), "Yes", Toast.LENGTH_SHORT).show();
				}
			});
	
    builder.show();
			 return true;	 
		case R.id.idsalir:
   //lblMensaje.setText("Opcion 3 pulsada!");;
	AlertDialog.Builder dialog=new AlertDialog.Builder(this);
	dialog.setMessage("¿Desea Salir?");
	dialog.setCancelable(false);
	dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Servicio_BT.stop();
					finish();
					
				}
			});
	dialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
	dialog.show();
   return true;    
		 }
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				switch(arg0.getId()){
				
				
				case R.id.on_motor:
					if(state_on_motor==0){
						sendMessage("a"); //97
						on_motor.setImageResource(R.drawable.on);
						state_on_motor=1;
					}else{
						sendMessage("b"); //98
						on_motor.setImageResource(R.drawable.off);
						state_on_motor=0;
					}
					
					break;
				
				}
	}
	@Override
	protected void onResume(){
		super.onResume();
		SensorManager sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensors=sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size()>0){
			sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	@Override
	protected void onStop(){
		SensorManager sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this);
		super.onStop();
	}
	

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generaterd method stub
		synchronized(this){
//			long current_time=event.timestamp;
			
			curX= (float) event.values[0];
			curY= (int) event.values[1];
			
			curX=(float)((int)(curX*100)/100.0);
			
			
			
			if(state_on_motor==1){
							
//			acX.setText("Acelerometro X: "+curX);
//			acY.setText("Acelerometro Y: "+curY);
//		    acZ.setText("Acelerometro Z: "+curZ);
//		
 
 
			
			if(curX>=6.5 && curX < 7.0){
				sendMessage("c"); //99
				aceler.setImageResource(R.drawable.x0);
			}else if(curX >= 6.0 && curX < 6.5){
				sendMessage("d"); //100
				aceler.setImageResource(R.drawable.x1);

			}else if(curX >= 5.5 && curX < 6.0 ){
				sendMessage("e"); //101
				aceler.setImageResource(R.drawable.x2);

			}else if(curX >= 5.0 && curX < 5.5){
				sendMessage("f"); //102
				aceler.setImageResource(R.drawable.x3);

			}else if(curX >= 4.5 && curX < 5.0){
				sendMessage("g"); //103
				aceler.setImageResource(R.drawable.x4);

			}else if(curX >= 4.0 && curX < 4.5){
				sendMessage("h"); //104
				aceler.setImageResource(R.drawable.x5);

			}else if(curX >= 3.5 && curX < 4.0){
				sendMessage("i"); //105
				aceler.setImageResource(R.drawable.x6);

			}else if(curX >= 3.0 && curX < 3.5){
				sendMessage("j"); //106
				aceler.setImageResource(R.drawable.x7);

			}else if(curX >= 2.5 && curX < 3.0){
				sendMessage("k"); //107
				aceler.setImageResource(R.drawable.x8);

			}else if(curX >= 2.0 && curX < 2.5){
				sendMessage("l"); //108
				aceler.setImageResource(R.drawable.x9);

			}
			
		
			
			switch(curY){
				case -5:
					sendMessage("m"); //109
					timon.setImageResource(R.drawable.y_5);
					
					
					break;
				case -4:
					sendMessage("n"); //110
					timon.setImageResource(R.drawable.y_4);
					break;
				case -3:
					sendMessage("o"); //111
					timon.setImageResource(R.drawable.y_3);
					break;
				case -2:
					sendMessage("p"); //112
					timon.setImageResource(R.drawable.y_2);
					break;
				case -1:
					sendMessage("q"); //113
					timon.setImageResource(R.drawable.y_1);
					break;
				case 0:
					timon.setImageResource(R.drawable.y0);
					break;	
				case 1:
					sendMessage("r"); //114
					timon.setImageResource(R.drawable.y1);
					break;
				case 2:
					sendMessage("s"); //115
					timon.setImageResource(R.drawable.y2);
					break;
				case 3:
					sendMessage("t"); //116
					timon.setImageResource(R.drawable.y3);
					break;
				case 4:
					sendMessage("u"); //117
					timon.setImageResource(R.drawable.y4);
					break;
				case 5:
					sendMessage("v"); //118
					timon.setImageResource(R.drawable.y5);
					break;
			}
			
			
			}
		}
	

	}
	
	private void sendMessage(String message) {
		// TODO Auto-generated method stub
		if(Servicio_BT.getState()==ConexionBT.STATE_CONNECTED){
			if(message.length()>0){
				byte[] send = message.getBytes(); //obtenemos los bytes del mensaje
				if(D) Log.e(TAG,"Mensaje enviado: "+message);
				Servicio_BT.write(send);
				}
		}
	}// fin del mensaje



	public void onStart(){
		super.onStart();
		ConfigBT();
	}
	public void onDestroy(){
		super.onDestroy();
		if(Servicio_BT!=null)
			Servicio_BT.stop(); //desconectamos el servicio
	}
	public void ConfigBT() {
		// TODO Auto-generated method stub
		//obtenemos el adaptador bluetooth
		AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
		if(AdaptadorBT.isEnabled()){ // si el BT esta encendido
			if(Servicio_BT == null){ // y el servicio es nulo, invocamos el Servicio_BT
				Servicio_BT= new ConexionBT(this,mHandler);
			}
		}else{
			if(D) Log.e("Setup","Bluetooth apagado..");
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth,REQUEST_ENABLE_TB);
		}
	}
	

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// una vez que se ha realizado una actividad regresa un resulado
		switch(requestCode){
		case REQUEST_ENABLE_TB: // respuesta de intento de encendido
			if(resultCode == Activity.RESULT_OK){ // BT esta acitvado e iniciamos servicios
				ConfigBT();
			}else{
				finish();
			}
			break;
		}
	}

	private void connect(String mMAC) {
		// TODO Auto-generated method stub
		if(stateConnect=="0"){
			if(D) Log.e("Conexion","Conectando");
			vibrador = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrador.vibrate(200);
			BluetoothDevice device=AdaptadorBT.getRemoteDevice(mMAC);
			Servicio_BT.connect(device);
			
			//btn_connect.setImageResource(R.drawable.bluon);
			
		
			stateConnect="1";
		}else{
			if(Servicio_BT != null) 
				

			Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();

			Servicio_BT.stop();	
			stateConnect ="0";
		}
		
		return;
	}
	
	final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
				switch(msg.what){
					case Mensaje_Escrito:
						byte[] writeBuf = (byte[]) msg.obj;
						String writeMessage = new String(writeBuf);
						if(D) Log.e(TAG,"Message_write =w= "+writeMessage);
					break;
					
					case Mensaje_Leido:
						byte[] readBuf = (byte[]) msg.obj;
						//Construye un Sting de los bytes validos en el buffer
						String readMessage = new String(readBuf, 0, msg.arg1);
						
						
						if(D) Log.e(TAG, "Message_read =R= "+readMessage);
						break;
					case Mensaje_Nombre_Dispositivo:
						mConnectedDeviceName=msg.getData().getString(DEVICE_NAME);
						Toast.makeText(getApplicationContext(), "Conectado con "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
						seleccionador= true;
						break;
					case Mensaje_TOAST:
						Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
						break;
					case MESSAGE_Desconectado:
						if(D) Log.e("Conexion","Desconectados");
						seleccionador = false;
						break;
					case Mensaje_Estado_Cambiado:
						if(D) Log.e("Estado BT","Escuchando conexiones entrantes");
						
						
						
							
						
						break;	
				}
		}
	};		
}
