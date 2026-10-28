# 🍔 BurgerCraft — Forge Burger App

> Trabalho de Desenvolvimento de Aplicativos Móveis

Aplicativo Android nativo em **Kotlin + Jetpack Compose (Material 3)** para pedidos em uma hamburgueria artesanal.

## ▶️ Como rodar

1. Abra a pasta do projeto no **Android Studio** (versão com suporte ao AGP 9.3 / compileSdk 37).
2. Aguarde o *Gradle Sync* baixar as dependências.
3. Rode o módulo `app` num emulador ou celular com Android 7.0 (API 24) ou superior.

Ou pela linha de comando: `./gradlew assembleDebug`.

## 📱 Telas (10)

| # | Tela | Arquivo | O que faz |
|---|------|---------|-----------|
| 0 | Login | `LoginScreen.kt` | Entrada com dois perfis de demonstração: **Administrador** (`admin` / `admin123`) cadastra e remove; **Cliente** (`user` / `1234`) só compra. |
| 1 | Início (Cardápio) | `MenuScreen.kt` | **Lista de Produtos** (LazyColumn + Card, em grade de 2), filtro por categoria, ordenar por preço. Toque abre os detalhes; o lápis **edita** e **segurar remove** (com confirmação). |
| 2 | Novo produto | `NovoProdutoScreen.kt` | Formulário para **adicionar Produto** (nome, preço numérico, descrição de várias linhas, ícone, categoria). |
| 3 | Detalhes do produto | `CustomizationScreen.kt` | Recebe `produtoId` pela rota. Ponto da carne (RadioButton), adicionais (Checkbox), observações, quantidade, **preço calculado**, categoria do produto (clicável) e favoritar. |
| 4 | Categorias | `CategoriasScreen.kt` | **Lista de Categorias** (LazyColumn + Card) com formulário para **adicionar** e **editar**, e lixeira para **remover**. |
| 5 | Detalhes da categoria | `CategoriaDetalhesScreen.kt` | Recebe `categoriaId` pela rota. Mostra os produtos da categoria e estatísticas calculadas (quantidade, preço médio, mais barato). |
| 6 | Buscar | `BuscaScreen.kt` | Campo de busca que filtra os produtos; sem texto, mostra atalhos das categorias. |
| 7 | Carrinho | `CartScreen.kt` | Itens personalizados, alterar quantidade/remover, endereço, forma de pagamento, subtotal + taxa + total. |
| 8 | Pedido confirmado | `PedidoScreen.kt` | Recebe o número do pedido pela rota. Resumo, tempo estimado e total. |
| 9 | Perfil | `PerfilScreen.kt` | Editar nome/endereço/telefone, favoritos e histórico de pedidos. |

Abas do **BottomNavigation**: Início · Buscar · Carrinho · Perfil (com contador de itens no carrinho).

## 🧱 Organização do código

- `Modelos.kt` — `Categoria` e `Produto` (agora `@Entity` do Room), `Adicional`, `ItemCarrinho`, `Pedido` e os dados iniciais usados para popular o banco na primeira execução.
- `AppDatabase.kt` — `@Database` com as duas entidades, em padrão Singleton (`companion object` + `@Volatile` + `synchronized`).
- `ProdutoDAO.kt` / `CategoriaDAO.kt` — `@Dao` com `inserir`, `buscarTodos`, `buscarPorId`, `atualizar` e `deletar` (todos `suspend`); o de produto também tem `buscarPorCategoria`.
- `OperacoesBanco.kt` — funções que chamam os DAOs dentro de `try/catch` com `Log.e(...)` e a carga inicial do banco.
- `BurgerViewModel.kt` — guarda as listas carregadas do banco, o estado de edição de produto, carrinho, pedidos, favoritos e login. É criado uma vez em `AppNavigation` e passado para todas as telas.
- `Rotas.kt` — objeto `Rotas` com todas as rotas nomeadas e funções para montar rotas com argumento (`Rotas.produto(id)`, `Rotas.categoria(id)`, `Rotas.pedido(numero)`).
- `AppNavigation.kt` — `Scaffold` + um único `NavHost`; a barra inferior só aparece nas rotas das abas.
- `BottomNavBar.kt` — `NavigationBar` com `NavigationBarItem`.
- `Componentes.kt` — peças reaproveitadas (TopAppBar com voltar, cabeçalho, seletor de quantidade, botões).
- `ui/theme/Tema.kt` — cores e tema escuro do app.

## 💾 Persistência com Room (Trabalho 3)

- Categorias e produtos ficam salvos num banco SQLite local (`app_database`) via **Room**. Adicionar, editar e remover pela interface grava no banco, e os dados continuam lá depois de fechar e reabrir o app.
- Na primeira execução o banco está vazio, então o app insere as categorias e produtos iniciais.
- Toda operação de banco roda em `CoroutineScope(Dispatchers.IO).launch { ... }`, e as telas de lista buscam os dados com `LaunchedEffect(Unit)`.
- As telas de detalhes (`CustomizationScreen` e `CategoriaDetalhesScreen`) buscam o item direto do banco com `buscarPorId` a partir do id recebido pela rota.
- Carrinho, pedidos, favoritos e perfil continuam em memória (fora do escopo do trabalho).
- Para voltar ao cardápio inicial, apague os dados do app (Configurações → Apps → Forge Burger → Armazenamento → Limpar dados) ou desinstale e rode de novo.

## ⭐ Complexidade extra nos Detalhes

- **Detalhes do produto**: calcula o preço em tempo real (preço base + adicionais × quantidade), mostra a **Categoria** do produto (dado da outra lista) e abre a tela dela, e adiciona o item personalizado ao carrinho.
- **Detalhes da categoria**: combina as duas listas — lista os produtos daquela categoria e calcula quantidade, preço médio e o mais barato; dá para cadastrar um produto já com a categoria escolhida.

## 🛠️ Tecnologias

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel · Room (SQLite) · Coroutines

## 📄 Relatórios

- Trabalho 3 (Room): [`docs/RELATORIO_TRABALHO_3.md`](docs/RELATORIO_TRABALHO_3.md)
- Trabalho 2 (navegação e listas): [`docs/RELATORIO_TRABALHO_2.md`](docs/RELATORIO_TRABALHO_2.md)
