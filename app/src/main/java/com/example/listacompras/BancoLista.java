package com.example.listacompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import androidx.annotation.Nullable;

import java.util.UUID;

public class BancoLista extends SQLiteOpenHelper {

    public BancoLista(@Nullable Context context) {
        super(context, "bancoCompromisso", null, 1);
    }
    //Criação da tabela no BD com campos Quantidade, Produto e Finalizado

    //O finalizado será para ver se o checkBox está marcado
    //Começa como '0'
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_bancoLista = "CREATE TABLE bancoLista(" +
                "QUANTIDADE TEXT," +
                "PRODUTO TEXT," +
                "FINALIZADO INTEGER)";

        db.execSQL(sql_bancoLista);
    }

    //Verificar a existencia do BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql_bancoLista_upgrade = "DROP TABLE IF EXISTS bancoLista;";

        db.execSQL(sql_bancoLista_upgrade);

        onCreate(db);
    }

    //Função insereProduto no BD
    //Recebe duas String (Produto e Quantidade)
    //Coloca na tabela uma prod para compra
    //Seta o valoor de finalizado = 0, pois o checkBox não estará marcado de início

    public Boolean insereProduto(String quantidade, String descricao){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues valuesResposta = new ContentValues();
        int finalizado = 0;

        valuesResposta.put("QUANTIDADE", quantidade);
        valuesResposta.put("PRODUTO", descricao);
        valuesResposta.put("FINALIZADO", finalizado);

        //Insere os dados e me retorna caso der erro

        try {
            db.insertOrThrow("bancoLista", null, valuesResposta);
        } catch(SQLException e) {
            db.close();
            return false;
        }
        //Retorna Sucesso, deu bom, inseriu
        return true;
    }


    //Função Busca produto
    //Ao clicar no botão 'Enviar', atualiza para o usuário todos os itens cadastrados
    public void buscaProduto(Context context, LinearLayout exibe) {
        SQLiteDatabase db = getReadableDatabase();

        String sql_resp = "SELECT * FROM bancoLista";

        Cursor cursor = db.rawQuery(sql_resp, null);

        //Aqui percorre todos os itens cadastrados e chama a função geraLista passando
        //Quantidade, produto e finalizado

        if (cursor.moveToLast()) {
            String qtt = cursor.getString(cursor.getColumnIndexOrThrow("QUANTIDADE"));
            String produto = cursor.getString(cursor.getColumnIndexOrThrow("PRODUTO"));
            int finalizado = cursor.getInt(cursor.getColumnIndexOrThrow("FINALIZADO"));

            //exibe.append("\n" + hora + " -> " + descricao + "\n");

            geraLista(produto, qtt, finalizado, context, exibe);
        }

        db.close();

        cursor.close();
    }

    //Essa funcção é chamada somente ao inciar o App
    //Se tiver itens cadastrados, exibe
    //Se Nn tiver, terá que esperar o usuário cadastrar
    //Igual a funcção acima, porém é chamada só no início do app

    public void buscaProdutos(Context context, LinearLayout exibe) {
        SQLiteDatabase db = getReadableDatabase();

        String sql_resp = "SELECT * FROM bancoLista";

        Cursor cursor = db.rawQuery(sql_resp, null);

        if(cursor.getCount() == 0){
         Log.i("nada","Não há Compromisso(s)");
        } else {
            while(cursor.moveToNext()) {
                String qtt = cursor.getString(cursor.getColumnIndexOrThrow("QUANTIDADE"));
                String produto = cursor.getString(cursor.getColumnIndexOrThrow("PRODUTO"));
                int finalizado = cursor.getInt(cursor.getColumnIndexOrThrow("FINALIZADO"));
                geraLista(produto, qtt, finalizado, context, exibe);
            }
        }

        db.close();

        cursor.close();
    }

    //Função para verificar se todos os checkBox estão preenchidos
    //Se Nn tiver, não deixa finalizar
    //Percorre todos os campos 'Finaliza' cadastrado, vendo se é = 1

    public boolean verificaFinaliza() {
        SQLiteDatabase db = getReadableDatabase();

        String sql_resp = "SELECT FINALIZADO FROM bancoLista";

        Cursor cursor = db.rawQuery(sql_resp, null);

        boolean todosFinalizados = true;

        while (cursor.moveToNext()) {
            int finalizado = cursor.getInt(cursor.getColumnIndexOrThrow("FINALIZADO"));
            if (finalizado == 0) {
                // Se encontrar algum registro com FINALIZADO igual a 0, retorna false imediatamente
                todosFinalizados = false;
                break;
            }
        }

        cursor.close();
        db.close();

        return todosFinalizados;
    }

    //Este código define um método geraLista que cria uma interface de usuário dinâmica.
    //Ele recebe um produto, uma quantidade, um indicador de finalização, onde o item será exibido.
    //O método concatena o produto e a quantidade em uma string
    //cria um RelativeLayout como contêiner, e dentro deste adiciona um CheckBox e um TextView.
    //O CheckBox é configurado com base no valor de finalizado para indicar se está marcado ou não
    //enquanto o TextView exibe o texto do produto e quantidade.
    //Por fim, esses componentes são organizados dentro do RelativeLayout e adicionados ao layout.
    private void geraLista(String produto, String qtt, int finalizado,Context context, LinearLayout exibe){
            String produtoFinal = produto + " -> " + qtt;
            RelativeLayout relativeLayout = new RelativeLayout(context);
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            relativeLayout.setLayoutParams(relativeLayoutParams);

            CheckBox checkBox = new CheckBox(context);
            RelativeLayout.LayoutParams checkBoxParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            checkBoxParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            checkBoxParams.addRule(RelativeLayout.CENTER_VERTICAL);
            checkBox.setLayoutParams(checkBoxParams);
            checkBox.setId(View.generateViewId());

            if (finalizado == 1) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            TextView textView = new TextView(context);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            textView.setLayoutParams(textViewParams);
            textView.setText(produtoFinal);
            textView.setTextColor(Color.parseColor("#fefefe"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setId(View.generateViewId());

            //Este código define um Listener para o CheckBox criado anteriormente
            //Quando o CheckBox é clicado, ele verifica se a View clicada é um CheckBox
            //obtém o texto associado, localiza a posição da substring "->" nesse texto
            //e extrai a parte antes dessa substring.
            //Depois, chama o método updateTabela com essa parte do texto (o produto antes do "->")
            //para atualizar a tabela correspondente e registra no log para depuração.

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof CheckBox) {
                        CheckBox clickedCheckBox = (CheckBox) v;
                        String textoTextView = ((TextView) relativeLayout.getChildAt(1)).getText().toString();
                        int index = textoTextView.indexOf("->");
                        if (index != -1) {
                            String textoAntesDoSeta = textoTextView.substring(0, index).trim();
                            updateTabela(textoAntesDoSeta);
                            Log.d("CheckBox Clicado", "Texto até o caractere '->': " + textoAntesDoSeta);
                        }
                    }
                }
            });

            relativeLayout.addView(checkBox);
            relativeLayout.addView(textView);

            exibe.addView(relativeLayout);
    }

    //Este método updateTabela atualiza o status de finalização de um produto
    //Depos, executa uma consulta SQL para buscar o valor atual do campo FINALIZADO
    //para o produto especificado. Se o produto for encontrado
    //ele alterna o valor de FINALIZADO (0 para 1, ou 1 para 0).
    //Depois, tenta atualizar a tabela bancoLista com o novo valor de FINALIZADO
    //para o produto correspondente.
    public Boolean updateTabela(String produto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Cursor cursor = db.rawQuery("SELECT FINALIZADO FROM bancoLista WHERE PRODUTO = ?", new String[]{produto});
        int finalizado = 0;

        if (cursor.moveToFirst()) {
            finalizado = cursor.getInt(cursor.getColumnIndexOrThrow("FINALIZADO"));
        }
        cursor.close();

        finalizado = (finalizado == 0) ? 1 : 0;

        values.put("FINALIZADO", finalizado);
        try {
            db.update("bancoLista", values, "PRODUTO = ?", new String[]{produto});
        } catch (SQLException e) {
            db.close();
            return false;
        }

        return true;
    }

    //Remove todos os dados do BD e o próprio BD
    //Tira todos o itens da tela do usuário
    public Boolean removeBancoAgenda(Context context, LinearLayout exibe) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete("bancoLista", null, null);
            exibe.removeAllViews();
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
        return true;
    }
}
