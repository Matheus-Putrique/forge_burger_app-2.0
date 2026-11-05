# 🍔 BurgerCraft — Forge Burger App

> Trabalho de Desenvolvimento de Aplicativos Móveis

Aplicativo Android nativo em **Kotlin + Jetpack Compose (Material 3)** para pedidos em uma hamburgueria artesanal, estruturado com **Room + Arquitetura MVVM**.

## ▶️ Como rodar

1. Abra a pasta do projeto no **Android Studio** (versão com suporte ao AGP 9.3 / compileSdk 37).
2. Aguarde o *Gradle Sync* baixar as dependências.
3. Rode o módulo `app` num emulador ou celular com Android 7.0 (API 24) ou superior.

Ou pela linha de comando: `./gradlew assembleDebug`.

## 📱 Telas (10)

| # | Tela | Arquivo (`ui/`) | O que faz |
|---|------|-----------------|-----------|
| 0 | Login | `LoginScreen.kt` | Entrada com dois perfis de demonstração: **Administrador** (`admin` / `admin123`) cadastra, edita e remove; **Cliente** (`user` / `1234`) só compra. |
| 1 | Início (Cardápio) | `MenuScreen.kt` | **Lista de Produtos** (LazyColumn + Card, em grade de 2), filtro por categoria, ordenar por preço. Toque abre os detalhes; o lápis **edita** e a lixeira / toque longo **remove** (com confirmação). |
| 2 | Novo produto | `NovoProdutoScreen.kt` | Formulário para **adicionar e editar Produto** (nome, preço numérico, descrição de várias linhas, ícone, categoria). |
| 3 | Detalhes do produto | `CustomizationScreen.kt` | Recebe `produtoId` pela rota. Ponto da carne (RadioButton), adicionais (Checkbox), observações, quantidade, **preço calculado**, categoria do produto (clicável) e favoritar. |
| 4 | Categorias | `CategoriasScreen.kt` | **Lista de Categorias** (LazyColumn + Card) com formulário para **adicionar** e **editar**, e lixeira para **remover**. |
| 5 | Detalhes da categoria | `CategoriaDetalhesScreen.kt` | Recebe `categoriaId` pela rota. Mostra os produtos da categoria e estatísticas calculadas (quantidade, preço médio, mais barato). |
| 6 | Buscar | `BuscaScreen.kt` | Campo de busca que filtra os produtos; sem texto, mostra atalhos das categorias. |
| 7 | Carrinho | `CartScreen.kt` | Itens personalizados, alterar quantidade/remover, endereço, forma de pagamento, subtotal + taxa + total. |
| 8 | Pedido confirmado | `PedidoScreen.kt` | Recebe o número do pedido pela rota. Resumo, tempo estimado e total. |
| 9 | Perfil | `PerfilScreen.kt` | Editar nome/endereço/telefone, favoritos e histórico de pedidos. |

Abas do **BottomNavigation**: Início · Buscar · Carrinho · Perfil (com contador de itens no carrinho).

## 🧱 Arquitetura MVVM e Organização em Pacotes (Trabalho 4)

O código em `com.example.forge_burger` está dividido em camadas com responsabilidades claras:

- **`data/`**
  - `Modelos.kt` — `@Entity` `Categoria` e `Produto`, modelos de apoio (`Adicional`, `ItemCarrinho`, `Pedido`, `Perfil`) e dados iniciais do cardápio.
  - `CategoriaDAO.kt` / `ProdutoDAO.kt` — interfaces `@Dao` com `buscarTodos()` reativo retornando `Flow<List<...>>`, além de `inserir`, `buscarPorId`, `atualizar`, `deletar` e `buscarPorCategoria`.
  - `AppDatabase.kt` — `@Database` com as duas entidades em padrão Singleton (`companion object` + `@Volatile` + `synchronized`).
- **`data/repository/`**
  - `CategoriaRepository.kt` e `ProdutoRepository.kt` — única fonte de verdade dos dados; encapsulam o acesso aos DAOs com tratamento de erros (`try/catch` + `Log.e`) e carga inicial quando o banco está vazio.
- **`viewmodel/`**
  - `ProdutoViewModel.kt` + `ProdutoUiState.kt` + `ProdutoViewModelFactory.kt` — gerencia o estado reativo dos produtos, filtros, busca, detalhes e formulário de cadastro/edição via `StateFlow`.
  - `CategoriaViewModel.kt` + `CategoriaUiState.kt` + `CategoriaViewModelFactory.kt` — gerencia a lista reativa de categorias, detalhes e formulário de criação/edição via `StateFlow`.
  - `BurgerViewModel.kt` + `BurgerUiState.kt` — gerencia login, perfil, favoritos, carrinho e pedidos em memória também no padrão `_uiState` privado + `uiState` público (`StateFlow`).
- **`ui/` (e `ui/theme/`)**
  - Todas as telas `@Composable`, `AppNavigation.kt`, `Rotas.kt`, `BottomNavBar.kt`, `Componentes.kt` e `Tema.kt`. As telas apenas observam o `uiState` com `collectAsState()` e acionam métodos dos `ViewModel`, sem contato direto com `DAO`, `CoroutineScope` ou `Dispatchers.IO`.
- **Raiz (`com.example.forge_burger`)**
  - `MainActivity.kt` — inicializa o banco e os repositórios e inicia o `AppNavigation`.

## 💾 Persistência com Room + Reatividade com Flow

- Categorias e produtos ficam salvos num banco SQLite local (`app_database`) via **Room**.
- `buscarTodos()` nos DAOs retorna `Flow<List<...>>`: qualquer `inserir`, `atualizar` ou `deletar` reflete automaticamente nas telas sem precisar recarregar listas manualmente.
- Na primeira execução o banco está vazio, então os repositórios populam as categorias e produtos iniciais.
- Para voltar ao cardápio inicial, apague os dados do app (Configurações → Apps → Forge Burger → Armazenamento → Limpar dados) ou desinstale e rode de novo.

## 🛠️ Tecnologias

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · Arquitetura MVVM (`ViewModel`, `UiState`, `StateFlow`, `ViewModelFactory`, `Repository`) · Room (SQLite + `Flow`) · Coroutines

## 📄 Relatórios

- Trabalho 4 (MVVM): [`docs/RELATORIO_TRABALHO_4.md`](docs/RELATORIO_TRABALHO_4.md)
- Trabalho 3 (Room): [`docs/RELATORIO_TRABALHO_3.md`](docs/RELATORIO_TRABALHO_3.md)
- Trabalho 2 (navegação e listas): [`docs/RELATORIO_TRABALHO_2.md`](docs/RELATORIO_TRABALHO_2.md)
