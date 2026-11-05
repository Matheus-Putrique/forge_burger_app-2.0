# 🍔 BurgerCraft — Relatório do Trabalho 4 (Arquitetura MVVM)

> Desenvolvimento de Aplicativos Móveis · Kotlin + Jetpack Compose (Material 3) + Room + MVVM (`Repository`, `StateFlow` / `UiState`, `ViewModelFactory`)

<!-- ✏️ Trechos marcados com ✏️ precisam ser completados pelo trio (prints e vídeo). Apague estes avisos antes de entregar. -->

---

### 1. Como estava o projeto no Trabalho 3, e o que mudou na reorganização?

* **Antes (Trabalho 3):** todos os arquivos `.kt` (telas, `Entity`, `DAO`, `AppDatabase`, `OperacoesBanco` e `BurgerViewModel`) ficavam soltos na mesma pasta raiz (`com.example.forge_burger`). Além disso, as próprias telas `@Composable` criavam o banco (`AppDatabase.getDatabase(context)`), pegavam os DAOs, disparavam `CoroutineScope(Dispatchers.IO).launch { ... }`, chamavam `buscarTodos()` manualmente depois de cada inserção/edição/remoção e espalhavam o estado da interface em várias variáveis `remember { mutableStateOf(...) }`.
* **Agora (Trabalho 4):** reorganizamos o projeto em camadas seguindo a arquitetura **MVVM**, sem adicionar telas nem funcionalidades novas:
  * Os DAOs passaram a expor `buscarTodos()` retornando `Flow<List<...>>`, atualizando a interface sozinhos sempre que o banco muda.
  * Criamos um `Repository` para cada entidade (`CategoriaRepository` e `ProdutoRepository`), isolando o acesso ao Room.
  * Criamos `ViewModel` separados por responsabilidade (`ProdutoViewModel`, `CategoriaViewModel` e `BurgerViewModel`), cada um com seu `UiState` exposto via `StateFlow` (`_uiState` privado + `uiState` público) e suas `ViewModelFactory`.
  * As telas ficaram limpas: apenas observam o `uiState` com `collectAsState()` e chamam funções do `ViewModel` nas ações do usuário.

✏️ **Print da estrutura de pacotes no Android Studio (Trabalho 3 × Trabalho 4):**

<!-- <img src="prints/t4/pacotes_mvvm.png" width="320"> -->

---

### 2. Como o trio decidiu dividir os pacotes — o que foi para `ui`, `data`, `data.repository` e `viewmodel`?

Dividimos o pacote `com.example.forge_burger` em 4 pacotes principais (mantendo apenas a `MainActivity.kt` na raiz como ponto de entrada do Android):

| Pacote | Arquivos | Responsabilidade |
|---|---|---|
| `data` | `Modelos.kt`, `CategoriaDAO.kt`, `ProdutoDAO.kt`, `AppDatabase.kt` | As `@Entity` (`Categoria` e `Produto`), modelos auxiliares, dados iniciais do cardápio, interfaces `@Dao` com `Flow` e a classe `@Database` Singleton do Room. |
| `data.repository` | `CategoriaRepository.kt`, `ProdutoRepository.kt` | Única camada que conversa diretamente com os DAOs, tratando erros com `try/catch` e concentrando a carga inicial quando o banco está vazio. |
| `viewmodel` | `ProdutoUiState.kt`, `ProdutoViewModel.kt`, `ProdutoViewModelFactory.kt`, `CategoriaUiState.kt`, `CategoriaViewModel.kt`, `CategoriaViewModelFactory.kt`, `BurgerUiState.kt`, `BurgerViewModel.kt` | Guarda o estado das telas em `StateFlow<...UiState>`, executa operações assíncronas com `viewModelScope.launch` e injeta os repositórios via `ViewModelProvider.Factory`. |
| `ui` (e `ui.theme`) | `AppNavigation.kt`, `Rotas.kt`, `BottomNavBar.kt`, `Componentes.kt`, `LoginScreen.kt`, `MenuScreen.kt`, `NovoProdutoScreen.kt`, `CustomizationScreen.kt`, `CategoriasScreen.kt`, `CategoriaDetalhesScreen.kt`, `BuscaScreen.kt`, `CartScreen.kt`, `PedidoScreen.kt`, `PerfilScreen.kt`, `Tema.kt` | Apenas interface visual (`@Composable`) e navegação, sem `DAO`, sem `CoroutineScope` e sem `Dispatchers.IO`. |

---

### 3. Por que o `Repository` existe — o que ele resolve que não existia antes?

No Trabalho 3, várias telas diferentes (`MenuScreen`, `CategoriasScreen`, `NovoProdutoScreen`, `CustomizationScreen`, `CategoriaDetalhesScreen`) instanciavam o banco e chamavam os DAOs por conta própria. Se precisássemos mudar uma consulta, trocar o tratamento de erro ou um dia buscar o cardápio de uma API na internet em vez do SQLite local, teríamos que alterar código espalhado por quase todas as telas.

Com o `CategoriaRepository` e o `ProdutoRepository` como **única fonte de verdade**:
* As telas nem sabem que o Room existe;
* O `ViewModel` pede os dados apenas ao `Repository`;
* Se a origem dos dados mudar no futuro (por exemplo, sincronizar com servidor remoto), só o `Repository` precisa ser adaptado, mantendo `ViewModel` e telas intactos.

---

### 4. O que tem dentro do `UiState` de cada tela, e por que escolheram esses campos?

Substituímos as variáveis `remember { mutableStateOf(...) }` soltas nas telas por 3 classes `data class ...UiState` imutáveis:

* **`ProdutoUiState` (`ProdutoViewModel`):**
  * `listaProdutos` e `carregando`: lista vinda automaticamente do `Flow` do banco para desenhar o cardápio e a busca.
  * `categoriaFiltroId`, `ordenarPorPreco` e `produtoParaRemover`: controlam o chip selecionado, a ordenação por preço e o diálogo de confirmação de exclusão na `MenuScreen`.
  * `estouEditando`, `produtoEmEdicao`, `emojiCampo`, `nomeCampo`, `precoCampo`, `descricaoCampo` e `categoriaIdEscolhida`: guardam todo o formulário da `NovoProdutoScreen` (tanto para criar quanto para editar, sem perder os dados ao girar a tela).
  * `textoBusca`: texto digitado na `BuscaScreen`.
  * `produtoDetalhe`, `quantidadeDetalhe`, `pontoCarneEscolhido`, `adicionaisEscolhidos` e `observacaoDetalhe`: guardam o item aberto e as escolhas de personalização na `CustomizationScreen`.
  * `categoriaDetalheId` e `produtosDaCategoriaDetalhe`: lista filtrada exibida na `CategoriaDetalhesScreen`.
* **`CategoriaUiState` (`CategoriaViewModel`):**
  * `listaCategorias` e `carregando`: lista reativa de categorias vinda do `Flow` do Room.
  * `nomeCampo`, `emojiCampo`, `descricaoCampo`, `estouEditando` e `categoriaEmEdicao`: controlam o formulário de criar/editar categoria na `CategoriasScreen`.
  * `categoriaDetalhe`: categoria carregada por ID na `CategoriaDetalhesScreen`.
* **`BurgerUiState` (`BurgerViewModel`):**
  * Guarda os dados de sessão em memória do app: login (`perfilLogado`, `usuarioCampoLogin`, `senhaCampoLogin`, `mostrarSenhaLogin`, `erroLogin`), perfil (`nomeUsuario`, `endereco`, `telefone` e campos de edição), `carrinho`, `formaPagamentoEscolhida`, `pedidos` e `favoritos`.

---

### 5. Alguma funcionalidade quebrou durante a reorganização? Como encontraram e corrigiram?

* **Imports e acesso ao `R.drawable` após mover pacotes:** ao mover `Modelos.kt` para o pacote `data` e as telas para `ui`, referências que antes estavam no mesmo pacote (como `R.drawable.foto_classic_bacon` e `fotoDoProduto`) precisaram de imports explícitos entre os novos pacotes.
* **Carga inicial com `Flow`:** antes, `carregarProdutosDoBanco` buscava uma lista simples e, se viesse vazia, inseria o cardápio inicial. Ao mudar `buscarTodos()` para `Flow`, tomamos o cuidado de fazer essa verificação uma única vez na inicialização do `Repository` (com `buscarTodos().first()`), antes de ficar coletando o `Flow`. Assim, o cardápio inicial é criado na primeira abertura do app, mas se o administrador apagar produtos manualmente depois, eles não voltam sozinhos.
* **Atualização automática sem recarregar na mão:** antes era preciso chamar `buscarProdutos()` logo depois de `inserir`, `atualizar` ou `deletar`. Com o `Flow` no DAO coletado no `init` do `ViewModel`, removemos todas essas buscas manuais repetidas e a lista passou a atualizar sozinha.

✏️ **Vídeo curto / prints do app funcionando após refatoração MVVM (adicionar, listar, editar, remover e reabrir):**

<!-- Link do vídeo (Google Drive / YouTube não listado) e prints -->

---

### 6. Algum integrante do trio teve dificuldade em algum ponto? Como resolveram?

A maior mudança de mentalidade foi parar de usar `remember { mutableStateOf(...) }` e `CoroutineScope(Dispatchers.IO)` direto nas telas para concentrar tudo no `UiState` dentro do `ViewModel`, além de entender como a `ViewModelFactory` entrega o `Repository` para o `ViewModel`. Revisando o fluxo completo (`DAO com Flow → Repository → ViewModel + UiState → Tela com collectAsState`), ficou bem mais claro onde cada parte do código deveria ficar.
