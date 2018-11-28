package arvores;

import java.util.Objects;
import model.Gasto;
import model.Resultado;
import nos.PaginaArvoreB;

public class ArvoreB {

    private Resultado resultado;
    private PaginaArvoreB raiz;
    private Integer grau;

    public ArvoreB() {
    }

    public ArvoreB(Integer grau) {
        this.raiz = null;
        this.grau = grau;
    }

    public PaginaArvoreB getRaiz() {
        return raiz;
    }

    public void setRaiz(PaginaArvoreB raiz) {
        this.raiz = raiz;
    }

    public Integer getGrau() {
        return grau;
    }

    public void setGrau(Integer grau) {
        this.grau = grau;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public void insereGastos(Gasto vetor[], Resultado resultado) { // Recebe os gastos que devem ser inseridos
        this.resultado = resultado;
        long tempoInicial = System.nanoTime();
        for (int i = 0; i < vetor.length; i++) {
            inserir(vetor[i]);
        }
        resultado.setTempoGasto(System.nanoTime() - tempoInicial);
    }

    public void buscaGastos(Gasto vetor[], Resultado resultado) { // Recebe os gastos que devem ser buscados
        this.resultado = resultado;
        long tempoInicial = System.nanoTime();
        for (int i = 0; i < vetor.length; i++) {
            procurar(vetor[i].getIdGasto());
        }
        resultado.setTempoGasto((System.nanoTime() - tempoInicial));
    }

    public void excluirGastos(Gasto vetor[], Resultado resultado) { // Recebe os gastos que deve ser excluídos
        this.resultado = resultado;
        long tempoInicial = System.nanoTime();
        for (int i = 0; i < vetor.length; i++) {
            remover(vetor[i].getIdGasto());
        }
        resultado.setTempoGasto((System.nanoTime() - tempoInicial));
    }

    private void inserir(Gasto gasto) { // A implementação da inserção na Árvore B segue dois princípios: 
        /*
         Inicialmente, a árvore está vazia, somente cria-se uma página que recebe os gastos e essa página é feita raiz da árvore
         Caso não esteja, é necessário uma verificação seguinte: 
         1 - A página da raiz está cheia? Se sim, é preciso criar uma nova página e dividir os registros entre as duas páginas
         2 - Se não, somente inserir na página o gasto e aumentar o número de chaves na página
         */
        if (getRaiz() == null) { // Insere uma nova raiz
            PaginaArvoreB paginaAux = new PaginaArvoreB(this.getGrau(), true);
            paginaAux.getGastos()[0] = gasto;
            paginaAux.setNumChavesAtual(1);
            this.setRaiz(paginaAux);
        } else { // Insere na página que não está completa ou cria uma nova página, divide os registros e verifica qual é o lugar de inserção
            if (getRaiz().getNumChavesAtual() == (2 * this.getGrau() - 1)) {
                PaginaArvoreB pag = new PaginaArvoreB(this.getGrau(), false);
                pag.getFilhos()[0] = getRaiz();
                this.dividirPagina(0, getRaiz(), pag);
                Integer i = 0;
                if (pag.getGastos()[0].getIdGasto() < gasto.getIdGasto()) {
                    i++;
                }
                inserirEmPaginaNaoCompleta(pag.getFilhos()[i], gasto);
                this.setRaiz(this.getRaiz());
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            } else {
                inserirEmPaginaNaoCompleta(this.getRaiz(), gasto);
            }
        }
    }

    private void inserirEmPaginaNaoCompleta(PaginaArvoreB pagina, Gasto gasto) {
        /**
         * *
         * Nesse caso, sabemos que a página não está completa e a inserção
         * deverá ser feita no lugar correto da página Inicialmente, uma
         * verificação para saber se uma página é folha é feita, caso não seja,
         * é preciso chegar até uma página que seja folha para que a inserção
         * seja feita
         */
        Integer i = pagina.getNumChavesAtual() - 1;
        if (pagina.getEhFolha()) {
            while (i >= 0 && pagina.getGastos()[i].getIdGasto() > gasto.getIdGasto()) { // While que localiza a posição dentro da página em que deve ser inserido o novo gasto
                pagina.getGastos()[i + 1] = pagina.getGastos()[i];
                i--;
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            }
            pagina.getGastos()[i + 1] = gasto; // O gasto então é colocado na posição correta da página
            Integer numChaves = pagina.getNumChavesAtual();
            numChaves++;
            pagina.setNumChavesAtual(numChaves);
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        } else { // Localiza a posição que deve avançar para a página abaixo
            while (i >= 0 && pagina.getGastos()[i].getIdGasto() > gasto.getIdGasto()) {
                i--;
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            }
            if (pagina.getFilhos()[i + 1].getNumChavesAtual() == (2 * pagina.getGrau() - 1)) { // Verifica se a página que se deve inserir possui espaço, caso não possua, é gerada uma divisão do nós de gasto com outras páginas
                dividirPagina(i + 1, pagina.getFilhos()[i + 1], pagina);
                if (pagina.getGastos()[i + 1].getIdGasto() < gasto.getIdGasto()) {
                    i++;
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                }
            }
            inserirEmPaginaNaoCompleta(pagina.getFilhos()[i + 1], gasto); // A inserção então é realizada
        }
    }

    private void dividirPagina(Integer contador, PaginaArvoreB noAtual, PaginaArvoreB noAnterior) {
        /**
         * *
         * A divisão de páginas é necessário para que as chaves sejam
         * redistribuídas de forma que um novo nó possa ser inserido na posição
         * correta ou um nó que foi removido possa ser redistribuído na página
         * correta. Como explicado no relatório, durante o split (Dividir
         * página), um valor central sobre para o noAnterior
         */
        PaginaArvoreB aux = new PaginaArvoreB(noAtual.getGrau(), noAtual.getEhFolha()); // Uma página auxiliar é criada para que seja feita a mudança dos nós de gasto de página
        aux.setNumChavesAtual(noAnterior.getGrau() - 1);
        for (Integer i = 0; i < noAnterior.getGrau() - 1; i++) {
            aux.getGastos()[i] = noAtual.getGastos()[i + noAnterior.getGrau()];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        if (!aux.getEhFolha()) {
            for (Integer i = 0; i < noAnterior.getGrau(); i++) {
                aux.getFilhos()[i] = noAtual.getFilhos()[i + noAnterior.getGrau()];
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            }
        }

        auxDividirPagina(contador, noAtual, noAnterior, aux);
    }

    private void auxDividirPagina(Integer contador, PaginaArvoreB noAtual, PaginaArvoreB noAnterior, PaginaArvoreB aux) {
        /**
         * *
         * Atualiza-se então os números de chaves nas páginas e é terminado o
         * processo de armazenar chaves e filhos no novo nó, no auxiliar e no nó
         * anterior
         */
        Integer numChaves = noAnterior.getGrau() - 1;
        noAtual.setNumChavesAtual(numChaves);
        for (Integer i = noAnterior.getNumChavesAtual(); i >= contador + 1; i--) {
            noAnterior.getFilhos()[i + 1] = noAnterior.getFilhos()[i]; // Nó anterior recebe os filhos mudando de posição
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        noAnterior.getFilhos()[contador + 1] = aux; // O nó anterior tem um novo filho, a Página Árvore B criada na função anterior
        for (Integer i = noAnterior.getNumChavesAtual() - 1; i >= contador; i--) {
            noAnterior.getGastos()[i + 1] = noAnterior.getGastos()[i]; // Nó anterior recebe os gastos mudando de posição
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        noAnterior.getGastos()[contador] = noAtual.getGastos()[noAnterior.getGrau() - 1]; // A posição do meio sobe para a Página Anterior
        numChaves = noAnterior.getNumChavesAtual() + 1;
        noAnterior.setNumChavesAtual(numChaves);
        resultado.setNumTrocas(resultado.getNumTrocas() + 1);
    }

    private Boolean procurar(Integer valor) { // Função responsável por auxiliar na busca de um valor na árvore b
        if (getRaiz() == null) {  // Se a raiz for nula, sabe-se que a Árvore B está vazia e não será encontrado nenhum nó.
            return false;
        } else { // Tenta localizar de forma recursiva o valor desejado
            return procurarAux(getRaiz(), valor);
        }
    }

    private Boolean procurarAux(PaginaArvoreB pagina, Integer valor) {
        /**
         * *
         * Inicialmente, navega-se na árvore b até tentar se localizar a posição
         * em que deve-se ir até a página que possa ter o gasto desejado. Tal
         * artefato pode ser visto no primeiro while.
         */
        Integer contador = 0;
        while (contador < pagina.getNumChavesAtual() - 1 && valor > pagina.getGastos()[contador].getIdGasto()) { // Localiza a posição em que deve enviar a navegação por recursão do filho
            contador++;
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
        }
        if (Objects.equals(pagina.getGastos()[contador].getIdGasto(), valor)) { // Navegou-se de forma recursiva até que se localizou dentro da árvore b o valor desejado, ele está presente
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            return true;
        }
        if (pagina.getEhFolha()) { // Se for uma página folha e o nó dentro da página não foi localizado, sabe-se que ele não está na Árvore B
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            return false;
        }
        return procurarAux(pagina.getFilhos()[contador], valor); // Verifica se o nó está presente, utilizando recursão
    }

    private void remover(Integer valor) {
        if (getRaiz() == null) { // Se a raiz não possuir página, não há o que ser removido na Árvore B
            return;
        }
        removerAux(getRaiz(), valor); // A partir da raiz, tenta-se localizar o valor a ser removido
        if (getRaiz().getNumChavesAtual() == 0) { // Se a raiz for vazia, significa que a última página foi retirada da árvore b ou que ela não possui nenhum gasto
            if (getRaiz().getEhFolha()) { // Se realmente for folha, significa que a raiz estava vazia
                raiz = null;
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            } else { // Caso contrário, ela possui um filho e esse filho passa a ser a nova raiz.
                setRaiz(getRaiz().getFilhos()[0]);
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            }
        }
    }

    private void removerAux(PaginaArvoreB no, Integer valor) {
        /**
         * O auxiliar da remoção recebe um nó, inicialmente, é raiz e o valor
         * que se deseja remover. Navega na árvore b até encontrar esse valor ou
         * não conseguir localizá-lo, sendo impossível a remoção
         */
        Integer indiceValor = 0;
        while (indiceValor < no.getNumChavesAtual() && no.getGastos()[indiceValor].getIdGasto() < valor) { // Inicialmente, localiza-se a posição do possível valor dentro da árvore b
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            indiceValor++;
        }
        if (indiceValor < no.getNumChavesAtual() && Objects.equals(no.getGastos()[indiceValor].getIdGasto(), valor)) { // Nesse caso, uma verificação é realizada para saber se o nó desejado é o que está nessa posição. Se sim, ele foi encontrado
            if (no.getEhFolha()) { // Se for folha, somente é realizada a cópia das chaves sobrescrevendo os outros gastos e reduzindo-se o número de chaves atual 
                for (Integer i = indiceValor + 1; i < no.getNumChavesAtual(); i++) {
                    no.getGastos()[i - 1] = no.getGastos()[i];
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
                no.setNumChavesAtual(no.getNumChavesAtual() - 1);
            } else { // Caso não seja nó folha, algumas verificações são necessárias
                Integer valorAux = no.getGastos()[indiceValor].getIdGasto(); // Descobre-se o identificador do gasto
                if (no.getFilhos()[indiceValor].getNumChavesAtual() >= no.getGrau()) { // Se o grau e o número de chaves for igual no caso do nó antecessor, é possível substituir o gasto pelo seu antecessor
                    Gasto antecessor = antecessor(no, indiceValor);
                    no.getGastos()[indiceValor] = antecessor;
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                    removerAux(no.getFilhos()[indiceValor], antecessor.getIdGasto()); // Retira-se o antecessor da sua antiga posição
                } else if (no.getFilhos()[indiceValor + 1].getNumChavesAtual() >= no.getGrau()) { // Caso ele possua menos filhos que o grau do nó permite, é necessária a utilização do sucessor. O gasto desse sucessor é colocado na posição necessária
                    Gasto sucessor = sucessor(no, indiceValor);
                    no.getGastos()[indiceValor] = sucessor;
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                    removerAux(no.getFilhos()[indiceValor + 1], indiceValor); // Retira-se o sucessor da sua antiga posição
                } else { // Sabendo-se que o os filhos possuem menos chaves que o grau, é possível então realizar a reconstituição de um nó, unindo-se o sucessor e o antecessor
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 3);
                    reconstituiUnindo(no, indiceValor);
                    removerAux(no.getFilhos()[indiceValor], indiceValor); // Retira-se o nó recursivamente recursivamente.
                }
            }
        } else { // Nesse caso, o nó a ser removido não está na Página atual da árvore B, é preciso continuar navegando
            if (no.getEhFolha()) { // Se o nó for folha, sabe-se que esse nó nunca será encontrado.
                return;
            }
            Boolean aux; // A verificação abaixo é realizada para saber se a chave desejada está na última posição da página
            if (Objects.equals(indiceValor, no.getNumChavesAtual())) {
                aux = true;
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            } else {
                aux = false;
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
            }
            if (no.getFilhos()[indiceValor].getNumChavesAtual() < no.getGrau()) { // Caso ocorra do filho ter um grau menor que o da página, é preciso preencher o nó atual
                reconstitui(no, indiceValor);
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            }
            if (aux && indiceValor > no.getNumChavesAtual()) { // Caso tenha ocorrido a reconstituição, é necessária uma verificação acerca do número de chaves. Caso seja maior o índice que o número de chaves, é necessário navegar pelo filho anterior
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                this.removerAux(no.getFilhos()[indiceValor - 1], valor);
            } else { // Caso o contrário, navega-se no valor atual
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
                this.removerAux(no.getFilhos()[indiceValor], valor);
            }
        }
    }

    private Gasto antecessor(PaginaArvoreB no, Integer indiceValor) { // Função responsável por descobrir o filho da Página Árvore B antecessor, o gasto que antecede ao gasto do nó atual.
        PaginaArvoreB aux = no.getFilhos()[indiceValor];
        while (!aux.getEhFolha()) {
            aux = aux.getFilhos()[aux.getNumChavesAtual()];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        return aux.getGastos()[aux.getNumChavesAtual() - 1];
    }

    private Gasto sucessor(PaginaArvoreB no, Integer indiceValor) { // Função responsável por descobrir o filho da Página Árvore B sucesso, o gasto que sucede ao gasto do nó atual atual.
        PaginaArvoreB aux = no.getFilhos()[indiceValor + 1];
        while (!aux.getEhFolha()) {
            aux = aux.getFilhos()[0];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        return aux.getGastos()[0];
    }

    private void reconstitui(PaginaArvoreB no, Integer indiceValor) {
        /**
         * Função responsável por reconstituir o filho de um nó que possui o seu
         * número de chaves menor que que o grau do nó em questão. Utiliza duas
         * funções, pegando valores emprestados do antecessor e retirando dessa
         * página e pegando valores emprestados do sucessor e retirando dessa
         * página.
         */
        if (indiceValor != 0 && no.getFilhos()[indiceValor - 1].getNumChavesAtual() >= no.getGrau()) { // Se o filho anterior possui mais chaves que o grau, pode-se pegar emprestado desse nó, sem a necessidade de união posterior.
            utilizaEmprestadoAntecessor(no, indiceValor);
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
        } else if (!Objects.equals(indiceValor, no.getNumChavesAtual()) && no.getFilhos()[indiceValor + 1].getNumChavesAtual() >= no.getGrau()) { // Se o filho posterior possui mais chaves que o grau, pode-se pegar emprestado desse nó, sem a necessidade de união posterior.
            utilizaEmprestadoSucessor(no, indiceValor);
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
        } else { // Caso nenhuma das condições anteriores seja verdade, sabe-se que é necessário realizar a reconstituição unindo-se as páginas dos filhos.
            if (!Objects.equals(indiceValor, no.getNumChavesAtual())) { // O índice do valor não pode ser igual ao número de chaves do nó atual, caso ocorra de não ser, ele sofre a reconstituição sendo unido com seu próximo irmão
                reconstituiUnindo(no, indiceValor);
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 3);
            } else { // Caso contrário, sofre a reconstituição com seu irmão anterior
                reconstituiUnindo(no, indiceValor - 1);
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 4);
            }
        }
    }

    private void utilizaEmprestadoAntecessor(PaginaArvoreB no, Integer indiceValor) {
        /**
         * Função responsável por utilizar o nó anterior emprestado para
         * preencher o nó atual, preenchendo-se os filhos da Página Árvore B
         */
        PaginaArvoreB noAux = no.getFilhos()[indiceValor]; // Descobre o atual
        PaginaArvoreB noAux2 = no.getFilhos()[indiceValor - 1]; // Descobre o filho anterior
        for (Integer i = noAux.getNumChavesAtual() - 1; i >= 0; i--) { // Cópia dos gastos
            noAux.getGastos()[i + 1] = noAux.getGastos()[i]; // Recebe os gastos da posição i na posição i + 1
            resultado.setNumTrocas(resultado.getNumTrocas() + 1); // Abriu-se espaço na primeira posição
        }
        if (!noAux.getEhFolha()) { // Caso não seja folha, realiza-se a cópia dos filhos de Página Arvore B
            for (Integer i = noAux.getNumChavesAtual(); i >= 0; i--) {
                noAux.getFilhos()[i + 1] = no.getFilhos()[i];
                resultado.setNumTrocas(resultado.getNumTrocas() + 1); // Abriu-se espaço na primeira posição
            }
        }
        noAux.getGastos()[0] = noAux.getGastos()[indiceValor - 1]; // A posição do filho na posição 0 é atualizada com o antecessor
        if (!no.getEhFolha()) { // Caso não seja folha
            noAux.getFilhos()[0] = noAux2.getFilhos()[noAux2.getNumChavesAtual()]; // Recebe o filho dos filhos na posição que o antecede
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        no.getGastos()[indiceValor - 1] = noAux2.getGastos()[noAux2.getNumChavesAtual() - 1]; // Recebe também os gastos
        resultado.setNumTrocas(resultado.getNumTrocas() + 2);

        noAux.setNumChavesAtual(noAux.getNumChavesAtual() + 1); // É necessário atualizar o número de nós dos dois filhos
        noAux2.setNumChavesAtual(noAux2.getNumChavesAtual() - 1);
    }

    private void utilizaEmprestadoSucessor(PaginaArvoreB no, Integer indiceValor) {
        /**
         * Função responsável por utilizar o nó posterior emprestado para
         * preencher o nó atual, preenchendo-se os filhos da Página Árvore B
         */
        PaginaArvoreB noAux = no.getFilhos()[indiceValor]; // Descobre o nó atual
        PaginaArvoreB noAux2 = no.getFilhos()[indiceValor + 1]; // Descobre o nó posterior ao atual
        noAux.getGastos()[noAux.getNumChavesAtual()] = no.getGastos()[indiceValor]; // O último nó de gasto passa a ser o nó de gasto na posição de índice valor
        if (!noAux.getEhFolha()) { // Caso não seja folha
            noAux.getFilhos()[noAux.getNumChavesAtual() + 1] = noAux2.getFilhos()[0]; // O filho recebe na última posição o primeiro filho do seu irmão
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        no.getGastos()[indiceValor] = noAux2.getGastos()[0]; // Realiza-se então a passagem desse valor para o nó, recebendo o gasto do filho a direita na primeira posição
        resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        for (Integer i = 1; i < noAux2.getNumChavesAtual(); i++) { // Atualiza-se os nós de gasto do irmão, o qual havia sido modificado anteriormente
            noAux2.getGastos()[i - 1] = noAux2.getGastos()[i];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        if (!noAux2.getEhFolha()) { // Caso não seja folha, atualiza-se também os filhos do irmão que "perdeu" um filho
            for (Integer i = 1; i <= noAux2.getNumChavesAtual(); i++) {
                noAux2.getFilhos()[i - 1] = noAux2.getFilhos()[i];
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            }
        }
        noAux.setNumChavesAtual(noAux.getNumChavesAtual() + 1); // O número de chaves é atualizado no irmão que ganhou um nó
        noAux2.setNumChavesAtual(noAux2.getNumChavesAtual() - 1); // E no irmão que perdeu um nó, também é atualizado
    }

    private void reconstituiUnindo(PaginaArvoreB no, Integer indiceValor) {
        /**
         * A função abaixo realiza a união de duas páginas de árvore b. A função
         * também libera o irmão do filho do nó atual, pois realizou o merge dos
         * dados.
         */
        PaginaArvoreB noAux = no.getFilhos()[indiceValor]; // Inicialmente, recebe o filho atual
        PaginaArvoreB noAux2 = no.getFilhos()[indiceValor + 1]; // Recebe o filho na posição + 1
        noAux.getGastos()[no.getGrau() - 1] = no.getGastos()[indiceValor]; // O nó auxiliar na última posição recebe o nó na posição correta
        auxReconstituiUnindo(no, noAux, noAux2, indiceValor); // Função para transposição dos gastos
        noAux.setNumChavesAtual(noAux.getNumChavesAtual() + noAux2.getNumChavesAtual() + 1); // Atualiza o número de chaves do nó
        no.setNumChavesAtual(no.getNumChavesAtual() - 1); // Diminui o número de chaves do nó atual
    }

    public void auxReconstituiUnindo(PaginaArvoreB no, PaginaArvoreB noAux, PaginaArvoreB noAux2, Integer indiceValor) {
        /*
        * Função responsável por realizar a transposição dos dados de uma Pagina para outra Pagina
         */
        for (Integer i = 0; i < noAux2.getNumChavesAtual(); i++) { // Realiza a transposição dos gastos
            noAux.getGastos()[i + no.getGrau()] = noAux2.getGastos()[i];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        if (!noAux2.getEhFolha()) {
            for (Integer i = 0; i < noAux2.getNumChavesAtual(); i++) { // Realiza a transposição dos filhos
                noAux.getFilhos()[i + no.getGrau()] = noAux2.getFilhos()[i];
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            }
        }
        for (Integer i = indiceValor + 1; i < no.getNumChavesAtual(); i++) { // Faz o nó atual receber os gastos retirando o da última posição
            no.getGastos()[i - 1] = no.getGastos()[i];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
        for (Integer i = indiceValor + 2; i <= no.getNumChavesAtual(); i++) { // A mesma coisa que ocorre acima, contudo, com os filhos
            no.getFilhos()[i - 1] = no.getFilhos()[i];
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        }
    }
}
