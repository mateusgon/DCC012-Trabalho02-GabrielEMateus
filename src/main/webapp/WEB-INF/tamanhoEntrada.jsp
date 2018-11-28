<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="jspf/cabecalho.jspf" %>
<div class="container text-center">
    <form method="post">
        <div class="form-group row">
            <label>Tamanho dos elementos (Digite separado por v�rgula e sem espa�o, devem ser 6 n�meros) </label>
            <textarea class="form-control" name="quantidadeElementos" rows="3"></textarea>
            <input type="submit" value="Enviar" class="btn btn-success"/>
            <input type="reset" class="btn btn-secondary"/>
        </div>
    </form>
</div>
<%@include file="jspf/rodape.jspf" %>