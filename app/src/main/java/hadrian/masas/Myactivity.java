package hadrian.masas;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class Myactivity extends AppCompatActivity {

    Lista_isotopos lista = new Lista_isotopos();
    double masa_inicial=0.;
    double masa_final=0.;
    double conversion=931.494;
    String write_input="";
    String write_output="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            masa_inicial = savedInstanceState.getDouble("masa_inicial");
            masa_final = savedInstanceState.getDouble("masa_final");
            write_input = savedInstanceState.getString("write_input");
            write_output = savedInstanceState.getString("write_output");
            TextView salida = (TextView) findViewById(R.id.show_entrada);
            salida.setText(write_input);
            TextView salida2 = (TextView) findViewById(R.id.show_salida);
            salida2.setText(write_output);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InputStream is = getResources().openRawResource(R.raw.mass);
        lista.import_isotopes(is);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putDouble("masa_inicial",masa_inicial);
        savedInstanceState.putDouble("masa_final",masa_final);
        savedInstanceState.putString("write_input",write_input);
        savedInstanceState.putString("write_output",write_output);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void add_entrada(View view) {
        EditText editText = (EditText) findViewById(R.id.simbolo_entrada);
        EditText editnumero = (EditText) findViewById(R.id.numero_entrada);
        double masa;
        if ((editText.getText().length() !=0) && (editnumero.getText().length() !=0)) {
            String entrada = editText.getText().toString().trim();
            editText.setText("");
            String numero = editnumero.getText().toString().trim();
            editnumero.setText("");


            TextView salida = (TextView) findViewById(R.id.show_entrada);
            if ((masa=lista.get_mass(entrada,Integer.parseInt(numero)))!=0){
                masa_inicial+=masa;
                write_input+=entrada + "-" + Integer.parseInt(numero) + " ";
                salida.setText(write_input);
            }
            else {
                entrada = "Invalid ISotope";
                salida.setText(entrada);
            }
        }
    }
    public void reset_in (View view){
        TextView salida = (TextView) findViewById(R.id.show_entrada);
        masa_inicial=0;
        write_input="";
        salida.setText(write_input);
    }
    public void add_salida(View view) {
        EditText editText = (EditText) findViewById(R.id.simbolo_salida);
        EditText editnumero = (EditText) findViewById(R.id.numero_salida);
        double masa;
        if ((editText.getText().length() !=0) && (editnumero.getText().length() !=0)) {
            String entrada = editText.getText().toString().trim();
            editText.setText("");
            String numero = editnumero.getText().toString().trim();
            editnumero.setText("");


            TextView salida = (TextView) findViewById(R.id.show_salida);
            if ((masa=lista.get_mass(entrada,Integer.parseInt(numero)))!=0){
                masa_final+=masa;
                write_output+=entrada + "-" + Integer.parseInt(numero) + " ";
                salida.setText(write_output);
            }
            else {
                entrada = "Invalid ISotope";
                salida.setText(entrada);
            }
        }

    }
    public void reset_out (View view){
        masa_final=0;
        TextView salida = (TextView) findViewById(R.id.show_salida);
        write_output="";
        salida.setText(write_output);
    }

    public void calcular(View view){
        TextView salida = (TextView) findViewById(R.id.textView);
        salida.setText(Double.toString((masa_final-masa_inicial)*conversion)+
                "MeV \n");
        salida.setText(salida.getText().toString()+Double.toString(masa_final-masa_inicial)+
        "u");
    }
}


class Isotopo {
    String nombre;
    int z;
    double mass;
    public Isotopo(String nombre_n,int z_n,double mass_n){
        nombre=nombre_n;
        z=z_n;
        mass=mass_n;
    }
}

class Lista_isotopos {
    List <Isotopo> lista =new ArrayList <>();
    public Lista_isotopos(){
    }
    public void add_isotopo(Isotopo iso){
        lista.add(iso);
    }
    public double get_mass(String nombre, int z){
        for (int i=0;i<lista.size();i++){
            if (lista.get(i).nombre.equals(nombre) && lista.get(i).z==z){
                return lista.get(i).mass;
            }
        }
        return 0L;
    }
    public void import_isotopes(InputStream is){
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String formato;
        String name;
        int z;
        double masa;
        double factor=(double) 1e-6;
        try {

            while ((formato=br.readLine())!=null) {
                name=formato.substring(20,22).trim();
                z=Integer.parseInt(formato.substring(16, 19).trim());
                masa=Double.parseDouble(formato.substring(96, 99).trim());
                masa=masa+factor*Double.parseDouble(formato.substring(100, 110).trim());
                this.add_isotopo(new Isotopo(name, z, masa));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}