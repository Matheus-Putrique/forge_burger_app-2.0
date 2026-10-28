# 🍔 BurgerCraft — Relatório do Trabalho 3 (Room)

> Desenvolvimento de Aplicativos Móveis · Kotlin + Jetpack Compose (Material 3) + Room

<!-- ✏️ Trechos marcados com ✏️ precisam ser completados pelo trio (texto, prints e vídeos). Apague estes avisos antes de entregar. -->

---

### 1. Como estava o projeto no Trabalho 2 e o que mudou?

* **Antes (Trabalho 2):** as listas de **Categorias** e **Produtos** ficavam no `BurgerViewModel` como `mutableStateListOf`, preenchidas com dados fixos no código. Adicionar e remover funcionava, mas tudo ficava só na memória: ao fechar o app, as mudanças se perdiam e o cardápio voltava ao estado inicial. Não existia edição de produto nem de categoria.
* **Agora (Trabalho 3):** categorias e produtos ficam salvos num banco SQLite local, usando **Room**. As telas buscam os dados do banco quando abrem, e adicionar, **editar** e remover gravam direto no banco. O ViewModel continua existindo, mas agora só guarda a última lista lida do banco, para as outras telas (busca, perfil, carrinho) usarem.

✏️ **Print do app no Trabalho 2 × agora:**

<!-- <img src="prints/t3/antes_depois.png" width="300"> -->

---

### 2. Quais Entity, DAO e Database foram criados — e por quê?

**Entities** (`Modelos.kt`): reaproveitamos as duas data classes do Trabalho 2, adicionando `@Entity` e `@PrimaryKey(autoGenerate = true)`:

| Entity | Tabela | Campos | Por quê |
|---|---|---|---|
| `Categoria` | `categorias` | `id`, `nome`, `emoji`, `descricao` | São exatamente os dados que o card e a tela de detalhes da categoria mostram. |
| `Produto` | `produtos` | `id`, `nome`, `descricao`, `preco`, `categoriaId`, `emoji`, `nota`, `ingredientes`, `foto` | `categoriaId` liga o produto à categoria (é por ele que filtramos). `preco` é `Double` para calcular total e preço médio. `foto` guarda o **nome** da imagem (ex.: `"foto_classic_bacon"`), não o número do recurso (ver seção 4). |

**DAOs:** `CategoriaDAO.kt` e `ProdutoDAO.kt`, um por Entity, com os 4 métodos básicos como `suspend fun`: `@Insert inserir`, `@Query buscarTodos`, `@Update atualizar` e `@Delete deletar`.

**Database:** `AppDatabase.kt`, uma `abstract class AppDatabase : RoomDatabase()` com `@Database(entities = [Categoria::class, Produto::class])`, que expõe `categoriaDAO()` e `produtoDAO()`. Segue o padrão Singleton visto em aula (`companion object` + `@Volatile` + `synchronized`), para o app inteiro usar uma única conexão com o banco.

**Operações (`OperacoesBanco.kt`):** cada chamada de DAO fica numa função própria com `try/catch` e `Log.e(...)`, como no exemplo de Filmes. As telas sempre chamam essas funções dentro de `CoroutineScope(Dispatchers.IO).launch { ... }`, para não travar a interface.

---

### 3. Como estruturamos as queries do DAO?

Além dos 4 métodos básicos:

* **`buscarPorId(id)`** (nos dois DAOs): `SELECT * FROM produtos WHERE id = :id` e `SELECT * FROM categorias WHERE id = :id`. As telas de detalhes recebem só o **id** pela rota e buscam o item no banco, em vez de receber o objeto pronto:
  * `CustomizationScreen` (detalhes do produto) busca o produto e depois a categoria dele.
  * `CategoriaDetalhesScreen` (detalhes da categoria) busca a categoria.
* **`buscarPorCategoria(categoriaId)`** (no `ProdutoDAO`): `SELECT * FROM produtos WHERE categoriaId = :categoriaId`. A tela de detalhes da categoria usa essa consulta para listar os produtos daquela categoria direto do banco.
* **Carga inicial:** `carregarCategoriasDoBanco` e `carregarProdutosDoBanco` chamam `buscarTodos()`. Se o banco vier vazio (primeira vez que o app abre), inserem o cardápio inicial e buscam de novo.

---

### 4. Dificuldades na migração de `mutableStateListOf` para Room

* **Listas que viravam estado vindo do banco:** antes as telas mexiam direto no `mutableStateListOf`. Agora o ViewModel guarda `mutableStateOf<List<...>>`, e depois de cada inserir, atualizar ou deletar a tela busca a lista de novo no banco e atualiza o ViewModel, para a interface mostrar o que está realmente salvo.
* **Edição de verdade:** no Trabalho 2 não havia edição. Seguimos o padrão da aula (`estouEditando` + item em edição): o mesmo formulário serve para criar e editar, e o botão chama `atualizar` ou `inserir` conforme o caso.
* **Foto do produto:** primeiro guardávamos a foto no banco como o número do recurso (`R.drawable.foto_x`). Esse número é gerado pelo Android a cada build e pode mudar quando alguém adiciona ou remove uma imagem. Aí o banco ficaria com números antigos, e o app mostraria a foto errada ou fecharia. Passamos a salvar o **nome** da foto como `String`, com uma tabela no código (`fotosProdutos`) que converte o nome no recurso certo. Como o formato da tabela mudou, subimos a versão do banco para 2 com `fallbackToDestructiveMigration`, para o Room recriar o banco em vez de dar erro.
* ✏️ **O que mais quebrou no caminho e como resolvemos:** (ex.: erro de configuração do KSP, ordem do sync do plugin, tela que não atualizava depois de salvar...)

✏️ **Prints/vídeo das etapas:**

<!--
| Adicionando | Editando | Removendo |
|:--:|:--:|:--:|
| <img src="prints/t3/adicionar.png" width="180"> | <img src="prints/t3/editar.png" width="180"> | <img src="prints/t3/remover.png" width="180"> |
-->

---

### 5. Teste de persistência (adicionar → fechar → reabrir)

Passo a passo do teste:

1. Entrar como administrador (`admin` / `admin123`).
2. Cadastrar um produto novo em **Novo lanche**.
3. Fechar o app completamente (tirar da lista de apps recentes).
4. Abrir de novo e entrar: o produto continua no cardápio.
5. O mesmo vale para editar e remover: as mudanças continuam depois de reabrir.

✏️ **Vídeo do teste:** <!-- link do vídeo (YouTube não listado / Google Drive) ou docs/videos/persistencia.mp4 -->

✏️ **Prints:**

<!--
| Antes de fechar | Depois de reabrir |
|:--:|:--:|
| <img src="prints/t3/persistencia_antes.png" width="200"> | <img src="prints/t3/persistencia_depois.png" width="200"> |
-->

---

### 6. Algum integrante teve dificuldade? Como resolveram?

✏️ (Resposta do trio, com as palavras de vocês.)
