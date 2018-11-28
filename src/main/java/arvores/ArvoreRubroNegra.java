package arvores;

import java.util.Objects;
import model.Gasto;
import model.Resultado;
import nos.NoRubroNegra;

public class ArvoreRubroNegra {

    private final Boolean corVermelha = true;
    private final Boolean corPreta = false;
    private final NoRubroNegra nil = new NoRubroNegra(-1, null, null);
    private Resultado resultado;
    private NoRubroNegra raiz;

    public ArvoreRubroNegra() {
        this.raiz = nil;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public void setRaiz(NoRubroNegra raiz) {
        this.raiz = raiz;
    }

    public NoRubroNegra getRaiz() {
        return raiz;
    }

    public void insereGastos(Gasto vetor[], Resultado resultado) { // Recebe os gastos que devem ser inseridos
        this.resultado = resultado;
        long tempoInicial = System.nanoTime();
        for (int i = 0; i < vetor.length; i++) {
            NoRubroNegra no = new NoRubroNegra(vetor[i].getIdGasto(), vetor[i], nil);
            auxInserir(no);
        }
        resultado.setTempoGasto(System.nanoTime() - tempoInicial);
    }

    public void buscaGastos(Gasto vetor[], Resultado resultado) { // Recebe os gastos que devem ser buscados
        this.resultado = resultado;
        long tempoInicial = System.nanoTime();
        for (int i = 0; i < vetor.length; i++) {
            NoRubroNegra no = new NoRubroNegra(vetor[i].getIdGasto(), vetor[i], nil);
            procurar(no, getRaiz());
        }
        resultado.setTempoGasto((System.nanoTime() - tempoInicial));
    }

    public void excluirGastos(Gasto vetor[], Resultado resultado) { // Recebe os gastos que deve ser excluídos
        this.resultado = resultado;
        long tempoInicial = System.nanoTime();
        for (int i = 0; i < vetor.length; i++) {
            NoRubroNegra no = new NoRubroNegra(vetor[i].getIdGasto(), vetor[i], nil);
            removerNo(no);
        }
        resultado.setTempoGasto((System.nanoTime() - tempoInicial));
    }

    private void auxInserir(NoRubroNegra noAtual) {
        /**
         * Função responsável por realizar a primeira parte da inserção na
         * árvore Rubro Negra. Inicialmente, uma verificação é feita para se
         * analisar se a raiz é nil. Como definido na bibliografia, nil é o nó
         * filho que todos os nós possuem. O nó nil possui sempre a cor preta.
         */
        if (getRaiz() == nil) { // Se a raiz for nil, significa que nenhum nó foi inserido na árvore ainda
            setRaiz(noAtual); // Então, a raiz passa a ser o nó recém-criado
            noAtual.setCor(corPreta); // A cor desse nó passa a ser preto
            noAtual.setPai(nil); // E por ser raiz, ele não possui pai.
        } else { // A raiz já possui um nó nesse caso, deve ser feita então a inserção.
            inserir(noAtual); // Utiliza a função inserção para inserir o nó atual 
            corrigirInsercao(noAtual); // Depois de realizar a inserção, é necessário verificar se as propriedades da árvore rubro-negra estão sendo atendidas
            getRaiz().setCor(corPreta); // Caso tenha ocorrido modificação na raiz, é preciso garantir que a condição que seja preta esteja sendo garantida
        }
    }

    private void inserir(NoRubroNegra noAtual) {
        /**
         * Utiliza uma inserção iterativa do nó, sem recursão. Inicialmente, a
         * raiz é recebida por um nó auxiliar. Percorre-se então, a partir da
         * raiz, a árvore tentando-se localizar a posição em que possa ser
         * inserido o nó já criado
         */
        Boolean auxInsercao = true;
        NoRubroNegra noAux = getRaiz();
        noAtual.setCor(corVermelha); // A cor de todo nó inserido, deve ser vermelho
        while (auxInsercao) { // Enquanto o nó não for inserido, deve-se continuar andando na árvore.
            if (noAtual.getChave() < noAux.getChave()) { // Sabe-se que é preciso navegar na árvore pela esquerda
                if (noAux.getFilhoEsquerda() == nil) { // Se o filho a esquerda for nil, achou a localização do novo nó 
                    noAux.setFilhoEsquerda(noAtual);
                    noAtual.setPai(noAux); // Coloca como pai o nó auxiliar
                    auxInsercao = false;
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 2);
                } else { // Avança pela esquerda
                    noAux = noAux.getFilhoEsquerda();
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
            } else if (noAtual.getChave() >= noAux.getChave()) { // Sabe-se que é preciso navegar na árvore pela direita
                if (noAux.getFilhoDireita() == nil) { // Se o filho a direita for nil, achou a localização do novo nó
                    noAux.setFilhoDireita(noAtual);
                    noAtual.setPai(noAux); // Coloca como pai o nó auxiliar
                    auxInsercao = false;
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 2);
                } else { // Avança pela direita
                    noAux = noAux.getFilhoDireita();
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
            }
        }
    }

    private void corrigirInsercao(NoRubroNegra noAtual) {
        /**
         * Nessa função, há a correção da inserção do nó verificando se as
         * propriedades que estão no relatório e estão presentes na literatura
         * estão sendo cumpridos. Existem duas cores, a vermelha que corresponde
         * a um boolean e é true. A segunda cor é a preta que corresponde a um
         * outro boolean que é false.
         */
        Boolean corrigirAux = true;
        while (corrigirAux && Objects.equals(noAtual.getPai().getCor(), corVermelha)) { // Enquanto a cor for vermelha, são realizadas verificações
            NoRubroNegra tioDoNo = nil; // o tio do nó, um auxiliar é preto
            if (noAtual.getPai() == noAtual.getPai().getPai().getFilhoEsquerda()) { // Uma verificação do pai do nó com o seu tio é realizada, se ambos forem iguais
                tioDoNo = noAtual.getPai().getPai().getFilhoDireita(); // Dessa forma, o tio do meu nó é o nó a direita
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                if (tioDoNo != nil && Objects.equals(tioDoNo.getCor(), corVermelha)) { // Se o meu tio não for nil e a sua cor for vermelha, é preciso garantir a propriedade da Árvore Rubro-Negra
                    noAtual.getPai().setCor(corPreta); // Meu pai passa a ser preto
                    tioDoNo.setCor(corPreta); // Meu tio também é preto
                    noAtual.getPai().getPai().setCor(corVermelha); // Meu avô passa a ser vermelho
                    noAtual = noAtual.getPai().getPai(); // Subo então para o meu avô e verifico a partir dele se as condições estão sendo garantidas
                    corrigirInsercao(noAtual);
                    corrigirAux = false;
                }
                if (corrigirAux && noAtual == noAtual.getPai().getFilhoDireita()) { // Se o nó atual for igual ao meu avô, eu avanço para a posição do meu pai.
                    noAtual = noAtual.getPai(); // Meu pai então passa a ser meu nó atual e uma rotação é necessária para balancear a árvore
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    rotacaoEsquerda(noAtual); // é feita uma rotação para a esquerda
                }
                if (corrigirAux) {
                    // A cor do meu pai passa a ser preto e a cor do meu avô passa a ser vermelha e o meu avô é rotacionado para que a árvore seja balanceada
                    noAtual.getPai().setCor(corPreta);
                    noAtual.getPai().getPai().setCor(corVermelha);
                    rotacaoDireita(noAtual.getPai().getPai());
                }

            } else { // Nesse caso, o pai do nó é igual ao meu tio a direita
                tioDoNo = noAtual.getPai().getPai().getFilhoEsquerda(); // O tio do nó recebe o seu tio a esquerda
                if (tioDoNo != nil && tioDoNo.getCor()) { // Se o meu tio não for nil e a sua cor for vermelha, é preciso garantir a propriedade da Árvore Rubro-Negra
                    noAtual.getPai().setCor(corPreta); // Meu pai passa a ser preto
                    tioDoNo.setCor(corPreta); // Meu tio também é preto
                    noAtual.getPai().getPai().setCor(corVermelha); // Meu avô passa a ser vermelho
                    noAtual = noAtual.getPai().getPai(); // Subo então para o meu avô e verifico a partir dele se as condições estão sendo garantidas
                    corrigirInsercao(noAtual);
                    corrigirAux = false;
                }
                if (corrigirAux && noAtual == noAtual.getPai().getFilhoEsquerda()) { // Se o nó atual for igual ao meu avô, eu avanço para a posição do meu pai.
                    noAtual = noAtual.getPai(); // Meu pai então passa a ser meu nó atual e uma rotação é necessária para balancear a árvore
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                    rotacaoDireita(noAtual); // é feita uma rotação para a direita
                }
                if (corrigirAux) {
                    // A cor do meu pai passa a ser preto e a cor do meu avô passa a ser vermelha e o meu avô é rotacionado para que a árvore seja balanceada
                    noAtual.getPai().setCor(corPreta);
                    noAtual.getPai().getPai().setCor(corVermelha);
                    rotacaoEsquerda(noAtual.getPai().getPai());
                }
            }
        }
    }

    private void rotacaoEsquerda(NoRubroNegra noAtual) {
        /**
         * Na rotação a esquerda, uma imagem utilizada no relatório poderá
         * elucidar o entendimento
         */
        if (noAtual.getPai() != nil) { // Nesse caso, não estamos tratando de um nó que é raiz
            if (noAtual == noAtual.getPai().getFilhoEsquerda()) { // Se o meu pai for diferente de nil, uma verificação para garantir que não seja raiz, é preciso verificar se o no atual é igual ao meu irmão
                noAtual.getPai().setFilhoEsquerda(noAtual.getFilhoDireita()); // Se sim, recebe um novo filho a esquerda
            } else { // Se não, recebe um novo filho a direita
                noAtual.getPai().setFilhoDireita(noAtual.getFilhoDireita());
            }
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            resultado.setNumTrocas(resultado.getNumTrocas() + 2);
            noAtual.getFilhoDireita().setPai(noAtual.getPai()); // Um novo pai é dado para o filho a direita
            noAtual.setPai(noAtual.getFilhoDireita()); // E é atualizado no nó atual o seu pai
            if (noAtual.getFilhoDireita().getFilhoEsquerda() != nil) {
                noAtual.getFilhoDireita().getFilhoEsquerda().setPai(noAtual);
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            }
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            noAtual.setFilhoDireita(noAtual.getFilhoDireita().getFilhoEsquerda());
            noAtual.getPai().setFilhoEsquerda(noAtual); // A rotação é realizada em dois níveis, com o pai do nó e no próprio nó, garantindo o balanceamento
            resultado.setNumTrocas(resultado.getNumTrocas() + 2);
        } else { // Nesse caso, é preciso rotacionar a raiz. O filho a direita da raiz passa a ser a nova raiz. É feita uma troca entre os pais, os filhos e esse novo nó raiz ainda pode ser vermelho nesse momento, sendo tratado mais adiante a sua cor
            NoRubroNegra auxDireita = getRaiz().getFilhoDireita();
            getRaiz().setFilhoDireita(auxDireita.getFilhoEsquerda());
            auxDireita.getFilhoEsquerda().setPai(getRaiz());
            getRaiz().setPai(auxDireita);
            auxDireita.setFilhoEsquerda(getRaiz());
            auxDireita.setPai(nil);
            setRaiz(auxDireita);
            resultado.setNumTrocas(resultado.getNumTrocas() + 7);
        }
        resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
    }

    private void rotacaoDireita(NoRubroNegra noAtual) {
        /**
         * A mesma questão da rotação esquerda dito anteriormente sobre as fotos
         * é válida aqui, o
         */
        if (noAtual.getPai() != nil) { // Nesse caso, estamos tratando de um nó que não é raiz
            if (noAtual == noAtual.getPai().getFilhoEsquerda()) {  // Se o meu pai for diferente de nil, uma verificação para garantir que não seja raiz, é preciso verificar se o no atual é igual ao meu irmão
                noAtual.getPai().setFilhoEsquerda(noAtual.getFilhoEsquerda());
            } else {
                noAtual.getPai().setFilhoDireita(noAtual.getFilhoEsquerda());
            }
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);

            noAtual.getFilhoEsquerda().setPai(noAtual.getPai()); // Um novo pai é dado para o filho a direita
            noAtual.setPai(noAtual.getFilhoEsquerda()); // E é atualizado no nó atual o seu pai
            resultado.setNumTrocas(resultado.getNumTrocas() + 2);
            if (noAtual.getFilhoEsquerda().getFilhoDireita() != nil) {
                noAtual.getFilhoEsquerda().getFilhoDireita().setPai(noAtual);
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            }
            noAtual.setFilhoEsquerda(noAtual.getFilhoEsquerda().getFilhoDireita());
            noAtual.getPai().setFilhoDireita(noAtual);
            resultado.setNumTrocas(resultado.getNumTrocas() + 2);
        } else { // Nesse caso, é preciso rotacionar a raiz. O filho a esquerda da raiz passa a ser a nova raiz. É feita uma troca entre os pais, os filhos e esse novo nó raiz ainda pode ser vermelho nesse momento, sendo tratado mais adiante a sua cor
            NoRubroNegra auxEsquerda = getRaiz().getFilhoEsquerda();
            getRaiz().setFilhoEsquerda(getRaiz().getFilhoEsquerda().getFilhoDireita());
            auxEsquerda.getFilhoDireita().setPai(getRaiz());
            getRaiz().setPai(auxEsquerda);
            auxEsquerda.setFilhoDireita(getRaiz());
            auxEsquerda.setPai(nil);
            setRaiz(auxEsquerda);
            resultado.setNumTrocas(resultado.getNumTrocas() + 7);
        }
        resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
    }

    private NoRubroNegra procurar(NoRubroNegra noUsado, NoRubroNegra possivelNo) {
        /**
         * Função utilizada para procurar um nó na árvore, recebe a raiz e o nó
         * que deseja ser procurado Funciona de forma recursiva
         */
        if (getRaiz() == nil) { // A raiz é vazia, o nó nunca será encontrado
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            return null;
        }
        if (noUsado.getChave() < possivelNo.getChave()) { // A chave do nó atual é menor do que o possível nó que está sendo investigado, caminha-se para a esquerda na árvore
            if (possivelNo.getFilhoEsquerda() != nil) {
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
                return procurar(noUsado, possivelNo.getFilhoEsquerda());
            }
        } else if (noUsado.getChave() > possivelNo.getChave()) { // A chave do nó atual é maior do que o possível nó que está sendo investigado, caminha-se para a direita na árvore
            if (possivelNo.getFilhoDireita() != nil) {
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
                return procurar(noUsado, possivelNo.getFilhoDireita());
            }
        } else if (Objects.equals(noUsado.getChave(), possivelNo.getChave())) { // Localizou-se o nó
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 3);
            return possivelNo;
        }
        return null;
    }

    private Boolean removerNo(NoRubroNegra noComOIdentificadorDoGasto) {
        /**
         * Função responsável por realizar a exclusão do nó informado. Funcion
         * de forma iterativa
         */
        noComOIdentificadorDoGasto = procurar(noComOIdentificadorDoGasto, getRaiz()); // São necessários então dois nós auxiliares
        if (noComOIdentificadorDoGasto == null) { // Inicialmente, é verificado se o nó realmente existe na árvore
            return false; // Se ele não existe, não é necessário fazer mais nada. 
        }

        NoRubroNegra aux; // São necessários então dois nós auxiliares
        NoRubroNegra aux2 = noComOIdentificadorDoGasto; // Esse nó recebe o nó que será removido
        Boolean corOriginal = aux2.getCor(); // Recebe a cor do nó que será removido

        if (noComOIdentificadorDoGasto.getFilhoEsquerda() == nil) { //Verifica-se se o filho a esquerda é igual a nil
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1); // Caso seja, avança pela direita
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            aux = noComOIdentificadorDoGasto.getFilhoDireita();
            exclusao(noComOIdentificadorDoGasto, noComOIdentificadorDoGasto.getFilhoDireita()); // Se sim, garante-se as propriedades do filho a direita e realiza as rotações necessárias
        } else if (noComOIdentificadorDoGasto.getFilhoDireita() == nil) { // Verifica-se se o filho a direita é igual a nil
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 2); // Caso seja, avança pela esquerda
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            aux = noComOIdentificadorDoGasto.getFilhoEsquerda();
            exclusao(noComOIdentificadorDoGasto, noComOIdentificadorDoGasto.getFilhoEsquerda()); // Se sim, garante-se as propriedades do filho a direita e realiza as rotações necessárias
        } else { // Não possui filhos que sejam nil, ou seja, não é folha.
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 3);
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            aux2 = menorNo(noComOIdentificadorDoGasto.getFilhoDireita()); // Descobre-se o menor nó na sub árvore a direita
            corOriginal = aux2.getCor(); // Realiza então a mudança desse nó, garantindo as características
            aux = aux2.getFilhoDireita();  // Recebe o filho a direita do menor nó a direita
            if (aux2.getPai() == noComOIdentificadorDoGasto) { // Se o pai do menor nó a direita for igual ao nó que se deseja remover
                aux.setPai(aux2); // Atualiza-se o pai
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            } else { // Se não, é necessário realizar rotações com o filho a direita
                exclusao(aux2, aux2.getFilhoDireita());
                aux2.setFilhoDireita(noComOIdentificadorDoGasto.getFilhoDireita());
                aux2.getFilhoDireita().setPai(aux2);
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
                resultado.setNumTrocas(resultado.getNumTrocas() + 2);
            }
            exclusao(noComOIdentificadorDoGasto, aux2); // Termina as correções sobre os filhos
            aux2.setFilhoEsquerda(noComOIdentificadorDoGasto.getFilhoEsquerda());
            aux2.getFilhoEsquerda().setPai(aux2);
            aux2.setCor(noComOIdentificadorDoGasto.getCor());
            resultado.setNumTrocas(resultado.getNumTrocas() + 3);
        }
        if (!corOriginal) { // Ainda é preciso, após as remoções, verificar se a cor do nó foi alterada, se sim, correções na árvore são necessárias.
            auxRemocao(aux);
        }
        return true;
    }
    
    private void exclusao(NoRubroNegra noDoGasto, NoRubroNegra noAuxiliar) {
        if (noDoGasto.getPai() == nil) { // Verifica se é a raiz
            setRaiz(noAuxiliar); // Se sim, atualiza a raiz
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        } else if (noDoGasto == noDoGasto.getPai().getFilhoEsquerda()) { // Move-se o filho a esquerda
            noDoGasto.getPai().setFilhoEsquerda(noAuxiliar);
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        } else { // Move-se o filho a direta
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 3);
            resultado.setNumTrocas(resultado.getNumTrocas() + 1);
            noDoGasto.getPai().setFilhoDireita(noAuxiliar);
        }
        resultado.setNumTrocas(resultado.getNumTrocas() + 1);
        noAuxiliar.setPai(noDoGasto.getPai()); // Atualiza o pai do noAuxiliar
    }

    private void auxRemocao(NoRubroNegra noAux) {
        Boolean auxRemocao = true; // Inicia variavel auxiliar como true
        while (auxRemocao && noAux != getRaiz() && !noAux.getCor()) { // Enquanto a variável auxiliar for true o nó auxiliar for diferente da raiz e possuir cor preta
            resultado.setNumComparacoes(resultado.getNumComparacoes() + 2); // Avança pela esquerda
            if (noAux == noAux.getPai().getFilhoEsquerda()) { // Se o nó auxiliar é igual ao filho a esqueda de seu pai
                NoRubroNegra aux = noAux.getPai().getFilhoDireita(); // Novo nó aux recebe o filho a direita do pai do noAux
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                if (aux.getCor()) { // Se aux for vermelho
                    aux.setCor(corPreta); // Muda pra preto
                    noAux.getPai().setCor(corVermelha); // Pai do noAux recebe cor vermelha
                    rotacaoEsquerda(noAux.getPai()); 
                    aux = noAux.getPai().getFilhoDireita(); // nó auxiliar recebe o filho a direita do pai de noAux
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
                if (!aux.getFilhoEsquerda().getCor() && !aux.getFilhoDireita().getCor()) { // Se ambos os filhos de aux tem cor preta  
                    aux.setCor(corVermelha); // Pinta aux de preto
                    noAux = noAux.getPai(); // noAux recebe seu próprio pai
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                    auxRemocao(noAux); // Chamada recursiva passando noAux
                    auxRemocao = false; // variável auxiliar é fechada
                } else if (!aux.getFilhoDireita().getCor()) { // Se não se apenas o filho a direita de aux é preto
                    aux.getFilhoEsquerda().setCor(corPreta); // Pinta o filho a esquerda de preto
                    aux.setCor(corVermelha); // Pinta aux de vermelho
                    rotacaoDireita(aux); 
                    aux = noAux.getPai().getFilhoDireita(); // aux recebe o filho a direita do pai de noAux
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 2);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
                if (auxRemocao && aux.getFilhoDireita().getCor()) { // Se a variável auxiliar ainda é true e o filho a direita de aux é vermelho
                    aux.setCor(noAux.getPai().getCor()); // aux recebe a cor do pai de noAux
                    noAux.getPai().setCor(corPreta); // o pai de noAux recebe cor preta
                    aux.getFilhoDireita().setCor(corPreta); // O filho a direita de aux recebe cor preta
                    rotacaoEsquerda(noAux.getPai()); 
                    noAux = getRaiz(); // noAux recebe a raiz
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
            } else {
                NoRubroNegra aux = noAux.getPai().getFilhoEsquerda(); // Se alguma das condições do if falhou
                resultado.setNumComparacoes(resultado.getNumComparacoes() + 2); // Avança pela esquerda
                resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                if (aux.getCor()) { // Se aux é vermelho
                    aux.setCor(corPreta); // pinta aux de preto
                    noAux.getPai().setCor(corVermelha); // Pinta o pai de noAux de vermelho
                    rotacaoDireita(noAux.getPai()); 
                    aux = noAux.getPai().getFilhoEsquerda(); // aux recebe o filho a esuqerda do pai de noAux
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1); 
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
                if (!aux.getFilhoDireita().getCor() && !aux.getFilhoEsquerda().getCor()) { // Se ambos os filhos de aux são pretos
                    aux.setCor(corVermelha); // aux recebe a cor vermelha
                    noAux = noAux.getPai(); // noAux recebe seu pai
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                    auxRemocao(noAux); // Chamada recursiva passando noAux
                    auxRemocao = false; // Variável auxiliar é fechada
                } else if (!aux.getFilhoEsquerda().getCor()) { // Se não se o filho a esquerda de aux é preto
                    aux.getFilhoDireita().setCor(corPreta); // O filho a direita de aux recebe a cor preta
                    aux.setCor(corVermelha); // Pinta aux de vermelho
                    rotacaoEsquerda(aux);
                    aux = noAux.getPai().getFilhoEsquerda(); // aux recebe o filho a esquerda do pai de noAux
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
                if (auxRemocao && aux.getFilhoEsquerda().getCor()) { // Se a variável auxiliar ainda é true e o filho a esquerda do nó aux é vermelho 
                    aux.setCor(noAux.getPai().getCor()); // aux recebe a cor do pai de noAux
                    noAux.getPai().setCor(corPreta); // o pai de noAux recebe a cor preta
                    aux.getFilhoEsquerda().setCor(corPreta); // o filho a esquerda de aux recebe a cor preta
                    rotacaoDireita(noAux.getPai()); 
                    noAux = getRaiz(); // noAux passa a ser a raiz
                    resultado.setNumComparacoes(resultado.getNumComparacoes() + 1);
                    resultado.setNumTrocas(resultado.getNumTrocas() + 1);
                }
            }
        }
        noAux.setCor(corPreta); // noAux recebe a cor preta
    }

    private NoRubroNegra menorNo(NoRubroNegra raizDaSubArvore) { // Função responsável por localizar o menorNó na subárvore a direita
        if (raizDaSubArvore.getFilhoEsquerda() != nil) {
            menorNo(raizDaSubArvore.getFilhoEsquerda());
        }
        return raizDaSubArvore;
    }

}

