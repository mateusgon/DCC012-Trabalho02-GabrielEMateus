package nos;

import model.Gasto;

public class NoRubroNegra {

    private final Boolean corVermelha = true;
    private final Boolean corPreta = false;
    private Integer chave = -1;
    private Gasto gasto;
    private Boolean cor;
    private NoRubroNegra filhoEsquerda, filhoDireita, pai;

    public NoRubroNegra(Integer chave, Gasto gasto, NoRubroNegra nil) {
        this.chave = chave;
        this.gasto = gasto;
        this.filhoEsquerda = nil;
        this.filhoDireita = nil;
        this.pai = nil;
        this.cor = corPreta;
    }

    public Integer getChave() {
        return chave;
    }

    public void setChave(Integer chave) {
        this.chave = chave;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public Boolean getCor() {
        return cor;
    }

    public void setCor(Boolean color) {
        this.cor = color;
    }

    public NoRubroNegra getFilhoEsquerda() {
        return filhoEsquerda;
    }

    public void setFilhoEsquerda(NoRubroNegra filhoEsquerda) {
        this.filhoEsquerda = filhoEsquerda;
    }

    public NoRubroNegra getFilhoDireita() {
        return filhoDireita;
    }

    public void setFilhoDireita(NoRubroNegra filhoDireita) {
        this.filhoDireita = filhoDireita;
    }

    public NoRubroNegra getPai() {
        return pai;
    }

    public void setPai(NoRubroNegra pai) {
        this.pai = pai;
    }
    
    
}
