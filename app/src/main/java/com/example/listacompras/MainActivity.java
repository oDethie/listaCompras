package com.example.listacompras;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //Buscando os elementos da tela e colocando como variáveis

    //Exemplo, pegando o botão 'Enviar' para realizar manipulações no código. Tipo, ao apertar
    //o código faz x coisas ou y verificações

    //Elementos de texto. Como, texto para inserir os valores (Produto/Quantidade)
    //Para realizar o envio das informações para o BD

    private TextView tituloProduto;
    private EditText produto;
    private TextView tituloQuantidade;
    private EditText quantidade;
    private Button botaoEnvia;
    private Button botaoFinaliza;
    private LinearLayout mostraTela;
    BancoLista bancoLista = new BancoLista(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Buscando Id dos elementos de tela e colocando em variáveis

        tituloProduto = findViewById(R.id.tituloProduto);
        produto = findViewById(R.id.produto);
        tituloQuantidade = findViewById(R.id.tituloQuantidade);
        quantidade = findViewById(R.id.quantidade);
        botaoEnvia = findViewById(R.id.botaoEnvia);
        botaoFinaliza = findViewById(R.id.botaoFinaliza);
        mostraTela = findViewById(R.id.mostraTela);

        //Função para buscar itens cadastrados no banco
        //Ela é chamada no início do código
        //Pois, se tiver itens, já exibe p/ o usuário

        bancoLista.buscaProdutos(MainActivity.this, mostraTela);

        //O botão 'Enviar' tem um Listener, toda vez que clicar nele, o código faz algo

        botaoEnvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Ao clicar no botão 'Enviar'
                //O código pega os valores que o usuário colocou em Produtos e Quantidade
                //Ambos pegam os valores e fazem um .trim(), essa função 'limpa' o texto
                //Ex: '   Pão de Queijo  ' -> Note que temos espaços em branco
                //A função limpa e deixa 'Pão de Queijo'

                String usuarioProduto = String.valueOf(produto.getText()).trim();
                String usuarioQuantidade = String.valueOf(quantidade.getText()).trim();

                //Há um limite de caracteres aceitos
                //Em Produto é até 15
                //Em quantidade é até 2 | aceita de 1 até 99
                //Se um dos dois for maior, mostra para o usúario uma mensagem de limite atingido

                //Caso ele não tenha colocado valores. Tipo, apertou 'Enviar' e deixou os campos vazio
                //Também mostra um aviso para verificar os campos digitados

                if(usuarioProduto.length() > 15 || usuarioQuantidade.length() > 2) showToast("Limite de caracteres atingido");
                else if(usuarioProduto.isEmpty() || (usuarioQuantidade.isEmpty() || usuarioQuantidade.equals("0"))){
                    showToast("Verifique os campos digitados");
                } else {

                    //Caso os valores forem corretos, vem aqui

                    //Chama a função do BD para inserí-los no BD
                    //A função retornará um boolean, 1 = sucesso, 0 = Erro

                    //Se for sucesso, ele limpa os valores de Produto e Quantidade, deixa vazio
                    //E chama a função para buscar no BD esses valores enviados
                    //Passará como parametro para a função de busca, o mostraTela
                    //Isso fará com que mostre para o usuário os dados no lugar correto
                    //Lugar correto = Quadrado preto abaixo do botão 'Enviar'

                    Log.i("msgP", usuarioProduto);
                    Log.i("msgQ", usuarioQuantidade);
                    Boolean verifica = bancoLista.insereProduto(usuarioQuantidade, usuarioProduto);
                    if (verifica) {
                        showToast("Salvo com sucesso");
                        produto.setText("");
                        quantidade.setText("");
                        bancoLista.buscaProduto(MainActivity.this, mostraTela);
                    } else showToast("Erro ao salvar");
                }
            }
        });

        //Coloca um Listener no botão 'Finalizar Lista'

        //Caso o usuário clique, fará algumas verificações
        //Ao cadastrar um produto/quantidade
        //Mostrará um checkBox ao lado
        //Após o usuário pegar o produto, terá que marcá-lo
        //Somente finalizará a lista, se todos estiverem marcados
        //Caso, tenha apenas um 'Não marcado', mostrará uma msg para o usuário

        //Caso, todos marcados, ele vai remover do BD as informações e excluirá da tela os prod/quantidade
        //Zerando a tela novamente
        //També, haverá um Boolean para confirmar se os itens foram apagados do BD

        botaoFinaliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean verificaFim = bancoLista.verificaFinaliza();
                if(verificaFim) {
                    Boolean verifica = bancoLista.removeBancoAgenda(MainActivity.this, mostraTela);
                    if (verifica) {
                        showToast("Lista finalizada com sucesso");
                    } else showToast("Erro ao excluir");
                } else {
                    showToast("Anda há itens pendentes");
                }
            }
        });
    }

    //Função para exibir ao usuário
    //Pequena caixinha abaixo da tela
    //Ex, mostrará "Lista finalizada com sucesso"
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}