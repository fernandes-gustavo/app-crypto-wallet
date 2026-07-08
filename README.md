# 💰 Crypto Wallet App

Este é um aplicativo Android nativo feito em **Kotlin** que permite acompanhar o mercado de criptomoedas em tempo real e gerenciar uma carteira de investimentos simulada. Criei esse projeto para colocar a mão na massa e praticar as ferramentas mais recomendadas hoje em dia para o desenvolvimento Android.

---

## 💡 O que eu aprendi e apliquei aqui:

* **Consumindo dados da internet (API):**
    * Para buscar os preços das criptomoedas em tempo real, fiz a integração com a API da **CoinGecko** utilizando o **Retrofit**. Foi uma ótima prática para entender como fazer requisições de rede, tratar as respostas e converter aquele JSON cheio de dados para objetos do Kotlin.
* **Organização com MVVM:**
    * Estruturei o projeto usando a arquitetura MVVM. Foi muito bacana ver na prática como separar a interface (as telas) da lógica do aplicativo deixa o código muito mais limpo e organizado.
* **Telas fluidas com Coroutines e StateFlow:**
    * Para garantir que o aplicativo não travasse enquanto busca os preços na internet, utilizei programação assíncrona. Isso deixa a atualização das moedas na tela bem rápida e sem engasgos.
* **Salvando os dados no celular (Room Database):**
    * Implementei o Room para criar um banco de dados local. Graças a isso, todas as transações que o usuário faz ficam salvas no próprio aparelho, então o saldo e as moedas continuam lá mesmo depois de fechar o app.
* **Cuidado especial com o Visual (UI/UX):**
    * Queria que o app não apenas funcionasse, mas fosse agradável de usar. Usei componentes do **Material Design** (como os *Bottom Sheets* que sobem na tela para a compra de moedas), adicionei o clássico gesto de arrastar para o lado para deletar uma moeda (*Swipe to Delete*) e criei uma animação bem suave para a lista aparecer sem dar aqueles "pulos" estranhos na tela quando atualiza.

---

## ⬇️ Baixe para Testar!

Você pode baixar o arquivo APK diretamente e instalar no seu celular para testar.

* **Arquivo APK:** [CryptoWallet-v1.0.apk](https://github.com/fernandes-gustavo/app-crypto-wallet/releases/download/v1.0/app-debug.apk)
* **Instruções de Instalação:** Lembre-se de permitir a instalação de aplicativos de "fontes desconhecidas" nas configurações de segurança do seu celular.
