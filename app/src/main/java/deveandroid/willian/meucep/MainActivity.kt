package deveandroid.willian.meucep

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import api.Api
import deveandroid.willian.meucep.databinding.ActivityMainBinding
import model.Endereco
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    // Variável binding
    private lateinit var binding: ActivityMainBinding

    // Função para prencher o formulário
    private fun setFormulario(logradouro: String, bairro: String, localidade: String, uf: String) {
        binding.editLogradouro.setText(logradouro)
        binding.editBairro.setText(bairro)
        binding.editCidade.setText(localidade)
        binding.editEstado.setText(uf)
    }

    private fun limparFormulario() {
        binding.editLogradouro.setText("")
        binding.editBairro.setText("")
        binding.editCidade.setText("")
        binding.editEstado.setText("")
    }

    private fun tratarErro(response: Int) {
        Toast.makeText(this, "Erro $response", Toast.LENGTH_SHORT).show()

        limparFormulario()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ********** Código começa aqui ********** //

        // Configuração do binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cor da barra de status
        window.statusBarColor = Color.parseColor("#FF4CAF58")

        // Configuração do retrofit
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://viacep.com.br/ws/")
            .build()
            .create(Api::class.java)

        // Configuração do btBuscarCEP
        binding.btBuscarCEP.setOnClickListener {
            val cep = binding.editCEP.text.toString()
            if(cep.isEmpty()) {
                Toast.makeText(this, "Preencha o campo CEP!", Toast.LENGTH_SHORT)
                    .show()

                limparFormulario()
            } else {
                retrofit.setEndereco(cep).enqueue(object : Callback<Endereco> {
                    override fun onResponse(
                        call: Call<Endereco>,
                        response: Response<Endereco>
                    ) {
                        if (response.code() == 200) {
                            val logradouro = response.body()?.logradouro.toString()
                            val bairro = response.body()?.bairro.toString()
                            val localidade = response.body()?.localidade.toString()
                            val uf = response.body()?.uf.toString()
                            setFormulario(logradouro, bairro, localidade, uf)
                        } else {
                            tratarErro(response.code())
                        }
                    }

                    override fun onFailure(call: Call<Endereco>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }

        // Configuração do btLimparCampos
        binding.btLimparCampos.setOnClickListener {
            binding.editCEP.setText("")
            limparFormulario()
        }
    }
}
