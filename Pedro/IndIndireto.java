/*********
 * ARVORE B+ SI
 * String chave, int dado
 * 
 * Os nomes dos mtodos foram mantidos em ingls
 * apenas para manter a coerência com o resto da
 * disciplina:
 * - boolean create(String chave, int dado)
 * - int read(String chave)
 * - boolean update(String chave, int dado)
 * - boolean delete(String chave)
 * 
 * Implementado pelo Prof. Marcos Kutova
 * v1.0 - 2018
 */


//package aed3;
import java.io.*;
import java.util.ArrayList;

// rvore B+ para ser usada como ndice indireto de algum arquivo de entidades
// CHAVE: String  (usado para algum atributo textual da entidade como Nome, Ttulo, ...)
// VALOR: Int     (usado para o identificador dessa entidade)

public class IndIndireto {

    private int  ordem;                 // Nmero mximo de filhos que uma pgina pode conter
    private int  maxElementos;          // Varivel igual a ordem - 1 para facilitar a clareza do cdigo
    private int  maxFilhos;             // Varivel igual a ordem para facilitar a clareza do cdigo
    private RandomAccessFile arquivo;   // Arquivo em que a rvore ser armazenada
    private String nomeArquivo;
    
    // Variveis usadas nas funções recursivas (j que no  possvel passar valores por referência)
    private String  chaveAux;
    private int     dadoAux;
    private long    paginaAux;
    private boolean cresceu;
    private boolean diminuiu;
    
    // Esta classe representa uma pgina da rvore (folha ou no folha). 
    private class Pagina {

        protected int      ordem;                 // Nmero mximo de filhos que uma pgina pode ter
        protected int      maxElementos;          // Varivel igual a ordem - 1 para facilitar a clareza do cdigo
        protected int      maxFilhos;             // Varivel igual a ordem  para facilitar a clareza do cdigo
        protected int      n;                     // Nmero de elementos presentes na pgina
        protected String[] chaves;                // Chaves
        protected int[]    dados;                 // Dados associados s chaves
        protected long     proxima;               // Prxima folha, quando a pgina for uma folha
        protected long[]   filhos;                // Vetor de ponteiros para os filhos
        protected int      TAMANHO_CHAVE;         // Tamanho da string mxima usada como chave
        protected int      TAMANHO_REGISTRO;      // Os elementos so de tamanho fixo
        protected int      TAMANHO_PAGINA;        // A pgina ser de tamanho fixo, calculado a partir da ordem

        // Construtor da pgina
        public Pagina(int o) {

            // Inicializaço dos atributos
            n = 0;
            ordem = o;
            maxFilhos = o;
            maxElementos = o-1;
            chaves = new String[maxElementos];
            dados  = new int[maxElementos];
            filhos = new long[maxFilhos];
            proxima = -1;
            
            // Criaço de uma pgina vzia
            for(int i=0; i<maxElementos; i++) {  
                chaves[i] = "";
                dados[i]  = -1;
                filhos[i] = -1;
            }
            filhos[maxFilhos-1] = -1;
            
            // Clculo do tamanho (fixo) da pgina
            // n -> 4 bytes
            // cada elemento -> 104 bytes (string + int)
            // cada ponteiro de filho -> 8 bytes (long)
            // ltimo filho -> 8 bytes (long)
            // ponteiro prximo -> 8 bytes
            TAMANHO_CHAVE = 100;
            TAMANHO_REGISTRO = 104;
            TAMANHO_PAGINA = 4 + maxElementos*TAMANHO_REGISTRO + maxFilhos*8 + 16;
        }
        
        // Como uma chave string tem tamanho varivel (por causa do Unicode),
        // provavelmente no ser possvel ter uma string de 100 caracteres.
        // Os caracteres excedentes (j que a pgina tem que ter tamanho fixo)
        // so preenchidos com espaços em branco
        private byte[] completaBrancos(String str) {
            byte[] aux;
            byte[] buffer = new byte[TAMANHO_CHAVE];
            aux = str.getBytes();
            int i=0; while(i<aux.length) { buffer[i] = aux[i]; i++; }
            while(i<TAMANHO_CHAVE) { buffer[i] = 0x20; i++; }
            return buffer;
        }
        
        // Retorna o vetor de bytes que representa a pgina para armazenamento em arquivo
        protected byte[] getBytes() throws IOException {
            
            // Um fluxo de bytes  usado para construço do vetor de bytes
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(ba);
            
            // Quantidade de elementos presentes na pgina
            out.writeInt(n);
            
            // Escreve todos os elementos
            int i=0;
            while(i<n) {
                out.writeLong(filhos[i]);
                out.write(completaBrancos(chaves[i]));
                out.writeInt(dados[i]);
                i++;
            }
            out.writeLong(filhos[i]);
            
            // Completa o restante da pgina com registros vazios
            byte[] registroVazio = new byte[TAMANHO_REGISTRO];
            while(i<maxElementos){
                out.write(registroVazio);
                out.writeLong(filhos[i+1]);
                i++;
            }

            // Escreve o ponteiro para a prxima pgina
            out.writeLong(proxima);
            
            // Retorna o vetor de bytes que representa a pgina
            return ba.toByteArray();
        }

        
        // Reconstri uma pgina a partir de um vetor de bytes lido no arquivo
        public void setBytes(byte[] buffer) throws IOException {
            
            // Usa um fluxo de bytes para leitura dos atributos
            ByteArrayInputStream ba = new ByteArrayInputStream(buffer);
            DataInputStream in = new DataInputStream(ba);
            byte[] bs = new byte[TAMANHO_CHAVE];
            
            // Lê a quantidade de elementos da pgina
            n = in.readInt();
            
            // Lê todos os elementos (reais ou vazios)
            int i=0;
            while(i<maxElementos) {
                filhos[i]  = in.readLong();
                in.read(bs);
                chaves[i] = (new String(bs)).trim();
                dados[i]   = in.readInt(); 
                i++;
            }
            filhos[i] = in.readLong();
            proxima = in.readLong();
        }
    }
    
    // ------------------------------------------------------------------------------
        
    
    public IndIndireto(int o, String na) throws IOException {
        
        // Inicializa os atributos da rvore
        ordem = o;
        maxElementos = o-1;
        maxFilhos = o;
        nomeArquivo = na;
        
        // Abre (ou cria) o arquivo, escrevendo uma raiz empty, se necessrio.
        arquivo = new RandomAccessFile(nomeArquivo,"rw");
        if(arquivo.length()<8) 
            arquivo.writeLong(-1);  // raiz empty
    }
    
    // Testa se a rvore est empty. Uma rvore empty  identificada pela raiz == -1
    public boolean empty() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        return raiz == -1;
    }
    
        
    // Busca recursiva por um elemento a partir da chave. Este metodo invoca 
    // o mtodo recursivo read1, passando a raiz como referência.
    public int read(String c) throws IOException {
        
        // Recupera a raiz da rvore
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        
        // Executa a busca recursiva
        if(raiz!=-1)
            return read1(c,raiz);
        else
            return -1;
    }
    
    // Busca recursiva. Este mtodo recebe a referência de uma pgina e busca
    // pela chave na mesma. A busca continua pelos filhos, se houverem.
    private int read1(String chave, long pagina) throws IOException {
        
        // Como a busca  recursiva, a descida para um filho inexistente
        // (filho de uma pgina folha) retorna um valor negativo.
        if(pagina==-1)
            return -1;
        
        // Reconstri a pgina passada como referência a partir 
        // do registro lido no arquivo
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
 
        // Encontra o ponto em que a chave deve estar na pgina
        // Primeiro passo - todas as chaves menores que a chave buscada so ignoradas
        int i=0;
        while(i<pa.n && chave.compareTo(pa.chaves[i])>0) {
            i++;
        }
        
        // Chave encontrada (ou pelo menos o ponto onde ela deveria estar).
        // Segundo passo - testa se a chave  a chave buscada e se est em uma folha
        // Obs.: em uma rvore B+, todas as chaves vlidas esto nas folhas
        // Obs.: a comparaço exata s ser possvel se considerarmos a menor string
        //       entre a chave e a string na pgina
        if(i<pa.n && pa.filhos[0]==-1 
                  && chave.compareTo(pa.chaves[i].substring(0,Math.min(chave.length(),pa.chaves[i].length())))==0) {
            return pa.dados[i];
        }
        
        // Terceiro passo - ainda no  uma folha, continua a busca recursiva pela rvore
        if(i==pa.n || chave.compareTo(pa.chaves[i])<0)
            return read1(chave, pa.filhos[i]);
        else
            return read1(chave, pa.filhos[i+1]);
    }
        
    // Atualiza recursivamente um valor a partir da sua chave. Este metodo invoca 
    // o mtodo recursivo update1, passando a raiz como referência.
    public boolean update(String c, int d) throws IOException {
        
        // Recupera a raiz da rvore
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        
        // Executa a busca recursiva
        if(raiz!=-1)
            return update1(c,d,raiz);
        else
            return false;
    }
    
    // Atualizaço recursiva. Este mtodo recebe a referência de uma pgina, uma
    // chave de busca e o dado correspondente a ela. 
    private boolean update1(String chave, int dado, long pagina) throws IOException {
        
        // Como a busca  recursiva, a descida para um filho inexistente
        // (filho de uma pgina folha) retorna um valor negativo.
        if(pagina==-1)
            return false;
        
        // Reconstri a pgina passada como referência a partir 
        // do registro lido no arquivo
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
 
        // Encontra o ponto em que a chave deve estar na pgina
        // Primeiro passo - todas as chaves menores que a chave buscada so ignoradas
        int i=0;
        while(i<pa.n && chave.compareTo(pa.chaves[i])>0) {
            i++;
        }
        
        // Chave encontrada (ou pelo menos o ponto onde ela deveria estar).
        // Segundo passo - testa se a chave  a chave buscada e se est em uma folha
        // Obs.: em uma rvore B+, todas as chaves vlidas esto nas folhas
        if(i<pa.n && pa.filhos[0]==-1 
                  && chave.compareTo(pa.chaves[i].substring(0,Math.min(chave.length(),pa.chaves[i].length())))==0) {
            pa.dados[i] = dado;
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            return true;
        }
        
        // Terceiro passo - ainda no  uma folha, continua a busca recursiva pela rvore
        if(i==pa.n || chave.compareTo(pa.chaves[i])<0)
            return update1(chave, dado, pa.filhos[i]);
        else
            return update1(chave, dado, pa.filhos[i+1]);
    }
        
    
    // Incluso de novos elementos na rvore. A incluso  recursiva. A primeira
    // funço chama a segunda recursivamente, passando a raiz como referência.
    // Eventualmente, a rvore pode crescer para cima.
    public boolean create(String c, int d) throws IOException {

        // Chave nao pode ser empty
        if(c.compareTo("")==0) {
            System.out.println( "Chave nao pode ser vazia" );
            return false;
        }
            
        // Carrega a raiz
        arquivo.seek(0);       
        long pagina;
        pagina = arquivo.readLong();

        // O processo de inclusao permite que os valores passados como referência
        // sejam substitudos por outros valores, para permitir a divisao de pginas
        // e crescimento da rvore. Assim, sao usados os valores globais chaveAux 
        // e dadoAux. Quando h uma divisao, a chave e o valor promovidos sao armazenados
        // nessas variveis.
        chaveAux = c;
        dadoAux = d;
        
        // Se houver crescimento, entao ser criada uma pgina extra e ser mantido um
        // ponteiro para essa pgina. Os valores tambm sao globais.
        paginaAux = -1;
        cresceu = false;
                
        // Chamada recursiva para a inserçao da chave e do valor
        // A chave e o valor nao sao passados como parâmetros, porque sao globais
        boolean inserido = create1(pagina);
        
        // Testa a necessidade de criaçao de uma nova raiz.
        if(cresceu) {
            
            // Cria a nova pgina que ser a raiz. O ponteiro esquerdo da raiz
            // ser a raiz antiga e o seu ponteiro direito ser para a nova pgina.
            Pagina novaPagina = new Pagina(ordem);
            novaPagina.n = 1;
            novaPagina.chaves[0] = chaveAux;
            novaPagina.dados[0]  = dadoAux;
            novaPagina.filhos[0] = pagina;
            novaPagina.filhos[1] = paginaAux;
            
            // Acha o espaço em disco. Nesta versao, todas as novas pginas
            // sao escrita no fim do arquivo.
            arquivo.seek(arquivo.length());
            long raiz = arquivo.getFilePointer();
            arquivo.write(novaPagina.getBytes());
            arquivo.seek(0);
            arquivo.writeLong(raiz);
        }
        
        return inserido;
    }
    
    
    // Funçao recursiva de inclusao. A funçao passa uma pgina de referência.
    // As inclusões sao sempre feitas em uma folha.
    private boolean create1(long pagina) throws IOException {
        
        // Testa se passou para o filho de uma pgina folha. Nesse caso, 
        // inicializa as variveis globais de controle.
        if(pagina==-1) {
            cresceu = true;
            paginaAux = -1;
            return false;
        }
        
        // Lê a pgina passada como referência
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
        
        // Busca o prximo ponteiro de descida. Como pode haver repetiçao
        // da primeira chave, a segunda tambm  usada como referência.
        // Nesse primeiro passo, todos os pares menores sao ultrapassados.
        int i=0;
        while(i<pa.n && chaveAux.compareTo(pa.chaves[i])>0) {
            i++;
        }
        
        // Testa se a chave j existe em uma folha. Se isso acontecer, entao 
        // a inclusao  cancelada.
        if(i<pa.n && pa.filhos[0]==-1 && chaveAux.compareTo(pa.chaves[i])==0) {
            cresceu = false;
            return false;
        }
        
        // Continua a busca recursiva por uma nova pgina. A busca continuar at o
        // filho inexistente de uma pgina folha ser alcançado.
        boolean inserido;
        if(i==pa.n || chaveAux.compareTo(pa.chaves[i])<0)
            inserido = create1(pa.filhos[i]);
        else
            inserido = create1(pa.filhos[i+1]);
        
        // A partir deste ponto, as chamadas recursivas j foram encerradas. 
        // Assim, o prximo cdigo s  executado ao retornar das chamadas recursivas.

        // A inclusao j foi resolvida por meio de uma das chamadas recursivas. Nesse
        // caso, apenas retorna para encerrar a recursao.
        // A inclusao pode ter sido resolvida porque a chave j existia (inclusao invlida)
        // ou porque o novo elemento coube em uma pgina existente.
        if(!cresceu)
            return inserido;
        
        // Se tiver espaço na pgina, faz a inclusao nela mesmo
        if(pa.n<maxElementos) {

            // Puxa todos elementos para a direita, começando do ltimo
            // para gerar o espaço para o novo elemento
            for(int j=pa.n; j>i; j--) {
                pa.chaves[j] = pa.chaves[j-1];
                pa.dados[j] = pa.dados[j-1];
                pa.filhos[j+1] = pa.filhos[j];
            }
            
            // Insere o novo elemento
            pa.chaves[i] = chaveAux;
            pa.dados[i] = dadoAux;
            pa.filhos[i+1] = paginaAux;
            pa.n++;
            
            // Escreve a pgina atualizada no arquivo
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            
            // Encerra o processo de crescimento e retorna
            cresceu=false;
            return true;
        }
        
        // O elemento nao cabe na pgina. A pgina deve ser dividida e o elemento
        // do meio deve ser promovido (sem retirar a referência da folha).
        
        // Cria uma nova pgina
        Pagina np = new Pagina(ordem);
        
        // Copia a metade superior dos elementos para a nova pgina,
        // considerando que maxElementos pode ser mpar
        int meio = maxElementos/2;
        for(int j=0; j<(maxElementos-meio); j++) {    
            
            // copia o elemento
            np.chaves[j] = pa.chaves[j+meio];
            np.dados[j] = pa.dados[j+meio];   
            np.filhos[j+1] = pa.filhos[j+meio+1];  
            
            // limpa o espaço liberado
            pa.chaves[j+meio] = "";
            pa.dados[j+meio] = 0;
            pa.filhos[j+meio+1] = -1;
        }
        np.filhos[0] = pa.filhos[meio];
        np.n = maxElementos-meio;
        pa.n = meio;
        
        // Testa o lado de inserçao
        // Caso 1 - Novo registro deve ficar na pgina da esquerda
        if(i<=meio) {   
            
            // Puxa todos os elementos para a direita
            for(int j=meio; j>0 && j>i; j--) {
                pa.chaves[j] = pa.chaves[j-1];
                pa.dados[j] = pa.dados[j-1];
                pa.filhos[j+1] = pa.filhos[j];
            }
            
            // Insere o novo elemento
            pa.chaves[i] = chaveAux;
            pa.dados[i] = dadoAux;
            pa.filhos[i+1] = paginaAux;
            pa.n++;
            
            // Se a pgina for folha, seleciona o primeiro elemento da pgina 
            // da direita para ser promovido, mantendo-o na folha
            if(pa.filhos[0]==-1) {
                chaveAux = np.chaves[0];
                dadoAux = np.dados[0];
            }
            
            // caso contrrio, promove o maior elemento da pgina esquerda
            // removendo-o da pgina
            else {
                chaveAux = pa.chaves[pa.n-1];
                dadoAux = pa.dados[pa.n-1];
                pa.chaves[pa.n-1] = "";
                pa.dados[pa.n-1] = 0;
                pa.filhos[pa.n] = -1;
                pa.n--;
            }
        } 
        
        // Caso 2 - Novo registro deve ficar na pgina da direita
        else {
            int j;
            for(j=maxElementos-meio; j>0 && chaveAux.compareTo(np.chaves[j-1])<0; j--) {
                np.chaves[j] = np.chaves[j-1];
                np.dados[j] = np.dados[j-1];
                np.filhos[j+1] = np.filhos[j];
            }
            np.chaves[j] = chaveAux;
            np.dados[j] = dadoAux;
            np.filhos[j+1] = paginaAux;
            np.n++;

            // Seleciona o primeiro elemento da pgina da direita para ser promovido
            chaveAux = np.chaves[0];
            dadoAux = np.dados[0];
            
            // Se nao for folha, remove o elemento promovido da pgina
            if(pa.filhos[0]!=-1) {
                for(j=0; j<np.n-1; j++) {
                    np.chaves[j] = np.chaves[j+1];
                    np.dados[j] = np.dados[j+1];
                    np.filhos[j] = np.filhos[j+1];
                }
                np.filhos[j] = np.filhos[j+1];
                
                // apaga o ltimo elemento
                np.chaves[j] = "";
                np.dados[j] = 0;
                np.filhos[j+1] = -1;
                np.n--;
            }

        }
        
        // Se a pgina era uma folha e apontava para outra folha, 
        // entao atualiza os ponteiros dessa pgina e da pgina nova
        if(pa.filhos[0]==-1) {
            np.proxima=pa.proxima;
            pa.proxima = arquivo.length();
        }

        // Grava as pginas no arquivos arquivo
        paginaAux = arquivo.length();
        arquivo.seek(paginaAux);
        arquivo.write(np.getBytes());

        arquivo.seek(pagina);
        arquivo.write(pa.getBytes());
        
        return true;
    }

    
    // Remoçao elementos na rvore. A remoçao  recursiva. A primeira
    // funçao chama a segunda recursivamente, passando a raiz como referência.
    // Eventualmente, a rvore pode reduzir seu tamanho, por meio da exclusao da raiz.
    public boolean delete(String chave) throws IOException {
                
        // Encontra a raiz da rvore
        arquivo.seek(0);       
        long pagina;                
        pagina = arquivo.readLong();

        // varivel global de controle da reduçao do tamanho da rvore
        diminuiu = false;  
                
        // Chama recursivamente a exclusao de registro (na chave1Aux e no 
        // chave2Aux) passando uma pgina como referência
        boolean excluido = delete1(chave, pagina);
        
        // Se a exclusao tiver sido possvel e a pgina tiver reduzido seu tamanho,
        // por meio da fusao das duas pginas filhas da raiz, elimina essa raiz
        if(excluido && diminuiu) {
            
            // Lê a raiz
            arquivo.seek(pagina);
            Pagina pa = new Pagina(ordem);
            byte[] buffer = new byte[pa.TAMANHO_PAGINA];
            arquivo.read(buffer);
            pa.setBytes(buffer);
            
            // Se a pgina tiver 0 elementos, apenas atualiza o ponteiro para a raiz,
            // no cabeçalho do arquivo, para o seu primeiro filho.
            if(pa.n == 0) {
                arquivo.seek(0);
                arquivo.writeLong(pa.filhos[0]);  
            }
        }
         
        return excluido;
    }
    

    // Funçao passa uma pgina de referência.
    // As exclusões sao sempre feitas em folhas e a fusao  propagada para cima.
    private boolean delete1(String chave, long pagina) throws IOException {
        
        // Declaraçao de variveis
        boolean excluido=false;
        int diminuido;
        
        // Testa se o registro nao foi encontrado na rvore, ao alcançar uma folha
        // inexistente (filho de uma folha real)
        if(pagina==-1) {
            diminuiu=false;
            return false;
        }
        
        // Lê o registro da pgina no arquivo
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);

        // Encontra a pgina em que a chave est presente
        // Nesse primeiro passo, salta todas as chaves menores
        int i=0;
        while(i<pa.n && chave.compareTo(pa.chaves[i])>0) {
            i++;
        }

        // Chaves encontradas em uma folha
        if(i<pa.n && pa.filhos[0]==-1 && chave.compareTo(pa.chaves[i])==0) {

            // Puxa todas os elementos seguintes para uma posiçao anterior, sobrescrevendo
            // o elemento a ser excludo
            int j;
            for(j=i; j<pa.n-1; j++) {
                pa.chaves[j] = pa.chaves[j+1];
                pa.dados[j] = pa.dados[j+1];
            }
            pa.n--;
            
            // limpa o ltimo elemento
            pa.chaves[pa.n] = "";
            pa.dados[pa.n] = 0;
            
            // Atualiza o registro da pgina no arquivo
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            
            // Se a pgina contiver menos elementos do que o mnimo necessrio,
            // indica a necessidade de fusao de pginas
            diminuiu = pa.n<maxElementos/2;
            return true;
        }

        // Se a chave nao tiver sido encontrada (observar o return true logo acima),
        // continua a busca recursiva por uma nova pgina. A busca continuar at o
        // filho inexistente de uma pgina folha ser alcançado.
        // A varivel diminudo mantem um registro de qual pgina eventualmente 
        // pode ter ficado com menos elementos do que o mnimo necessrio.
        // Essa pgina ser filha da pgina atual
        if(i==pa.n || chave.compareTo(pa.chaves[i])<0) {
            excluido = delete1(chave, pa.filhos[i]);
            diminuido = i;
        } else {
            excluido = delete1(chave, pa.filhos[i+1]);
            diminuido = i+1;
        }
        
        
        // A partir deste ponto, o cdigo  executado aps o retorno das chamadas
        // recursivas do mtodo
        
        // Testa se h necessidade de fusao de pginas
        if(diminuiu) {

            // Carrega a pgina filho que ficou com menos elementos do 
            // do que o mnimo necessrio
            long paginaFilho = pa.filhos[diminuido];
            Pagina pFilho = new Pagina(ordem);
            arquivo.seek(paginaFilho);
            arquivo.read(buffer);
            pFilho.setBytes(buffer);
            
            // Cria uma pgina para o irmao (da direita ou esquerda)
            long paginaIrmao;
            Pagina pIrmao;
            
            // Tenta a fusao com irmao esquerdo
            if(diminuido>0) {
                
                // Carrega o irmao esquerdo
                paginaIrmao = pa.filhos[diminuido-1];
                pIrmao = new Pagina(ordem);
                arquivo.seek(paginaIrmao);
                arquivo.read(buffer);
                pIrmao.setBytes(buffer);
                
                // Testa se o irmao pode ceder algum registro
                if(pIrmao.n>maxElementos/2) {
                    
                    // Move todos os elementos do filho aumentando uma posiçao
                    //  esquerda, gerando espaço para o elemento cedido
                    for(int j=pFilho.n; j>0; j--) {
                        pFilho.chaves[j] = pFilho.chaves[j-1];
                        pFilho.dados[j] = pFilho.dados[j-1];
                        pFilho.filhos[j+1] = pFilho.filhos[j];
                    }
                    pFilho.filhos[1] = pFilho.filhos[0];
                    pFilho.n++;
                    
                    // Se for folha, copia o elemento do irmao, j que o do pai
                    // ser extinto ou repetido
                    if(pFilho.filhos[0]==-1) {
                        pFilho.chaves[0] = pIrmao.chaves[pIrmao.n-1];
                        pFilho.dados[0] = pIrmao.dados[pIrmao.n-1];
                    }
                    
                    // Se nao for folha, rotaciona os elementos, descendo o elemento do pai
                    else {
                        pFilho.chaves[0] = pa.chaves[diminuido-1];
                        pFilho.dados[0] = pa.dados[diminuido-1];
                    }

                    // Copia o elemento do irmao para o pai (pgina atual)
                    pa.chaves[diminuido-1] = pIrmao.chaves[pIrmao.n-1];
                    pa.dados[diminuido-1] = pIrmao.dados[pIrmao.n-1];
                        
                    
                    // Reduz o elemento no irmao
                    pFilho.filhos[0] = pIrmao.filhos[pIrmao.n];
                    pIrmao.n--;
                    diminuiu = false;
                }
                
                // Se na puder ceder, faz a fusao dos dois irmaos
                else {

                    // Se a pgina reduzida nao for folha, entao o elemento 
                    // do pai deve ser copiado para o irmao
                    if(pFilho.filhos[0] != -1) {
                        pIrmao.chaves[pIrmao.n] = pa.chaves[diminuido-1];
                        pIrmao.dados[pIrmao.n] = pa.dados[diminuido-1];
                        pIrmao.filhos[pIrmao.n+1] = pFilho.filhos[0];
                        pIrmao.n++;
                    }
                    
                    
                    // Copia todos os registros para o irmao da esquerda
                    for(int j=0; j<pFilho.n; j++) {
                        pIrmao.chaves[pIrmao.n] = pFilho.chaves[j];
                        pIrmao.dados[pIrmao.n] = pFilho.dados[j];
                        pIrmao.filhos[pIrmao.n+1] = pFilho.filhos[j+1];
                        pIrmao.n++;
                    }
                    pFilho.n = 0;   // aqui o endereço do filho poderia ser incluido em uma lista encadeada no cabeçalho, indicando os espaços reaproveitveis
                    
                    // Se as pginas forem folhas, copia o ponteiro para a folha seguinte
                    if(pIrmao.filhos[0]==-1)
                        pIrmao.proxima = pFilho.proxima;
                    
                    // puxa os registros no pai
                    int j;
                    for(j=diminuido-1; j<pa.n-1; j++) {
                        pa.chaves[j] = pa.chaves[j+1];
                        pa.dados[j] = pa.dados[j+1];
                        pa.filhos[j+1] = pa.filhos[j+2];
                    }
                    pa.chaves[j] = "";
                    pa.dados[j] = -1;
                    pa.filhos[j+1] = -1;
                    pa.n--;
                    diminuiu = pa.n<maxElementos/2;  // testa se o pai tambm ficou sem o nmero mnimo de elementos
                }
            }
            
            // Faz a fuso com o irmo direito
            else {
                
                // Carrega o irmo
                paginaIrmao = pa.filhos[diminuido+1];
                pIrmao = new Pagina(ordem);
                arquivo.seek(paginaIrmao);
                arquivo.read(buffer);
                pIrmao.setBytes(buffer);
                
                // Testa se o irmao pode ceder algum elemento
                if(pIrmao.n>maxElementos/2) {
                    
                    // Se for folha
                    if( pFilho.filhos[0]==-1 ) {
                    
                        //copia o elemento do irmao
                        pFilho.chaves[pFilho.n] = pIrmao.chaves[0];
                        pFilho.dados[pFilho.n] = pIrmao.dados[0];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                        pFilho.n++;

                        // sobe o prximo elemento do irmao
                        pa.chaves[diminuido] = pIrmao.chaves[1];
                        pa.dados[diminuido] = pIrmao.dados[1];
                        
                    } 
                    
                    // Se nao for folha, rotaciona os elementos
                    else {
                        
                        // Copia o elemento do pai, com o ponteiro esquerdo do irmao
                        pFilho.chaves[pFilho.n] = pa.chaves[diminuido];
                        pFilho.dados[pFilho.n] = pa.dados[diminuido];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                        pFilho.n++;
                        
                        // Sobe o elemento esquerdo do irmao para o pai
                        pa.chaves[diminuido] = pIrmao.chaves[0];
                        pa.dados[diminuido] = pIrmao.dados[0];
                    }
                    
                    // move todos os registros no irmao para a esquerda
                    int j;
                    for(j=0; j<pIrmao.n-1; j++) {
                        pIrmao.chaves[j] = pIrmao.chaves[j+1];
                        pIrmao.dados[j] = pIrmao.dados[j+1];
                        pIrmao.filhos[j] = pIrmao.filhos[j+1];
                    }
                    pIrmao.filhos[j] = pIrmao.filhos[j+1];
                    pIrmao.n--;
                    diminuiu = false;
                }
                
                // Se naos
                else {

                    // Se a pgina reduzida nao for folha, entao o elemento 
                    // do pai deve ser copiado para o irmao
                    if(pFilho.filhos[0] != -1) {
                        pFilho.chaves[pFilho.n] = pa.chaves[diminuido];
                        pFilho.dados[pFilho.n] = pa.dados[diminuido];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                        pFilho.n++;
                    }
                    
                    // Copia todos os registros do irmAo da direita
                    for(int j=0; j<pIrmao.n; j++) {
                        pFilho.chaves[pFilho.n] = pIrmao.chaves[j];
                        pFilho.dados[pFilho.n] = pIrmao.dados[j];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[j+1];
                        pFilho.n++;
                    }
                    pIrmao.n = 0;   // aqui o endereço do irmao poderia ser incluido em uma lista encadeada no cabeçalho, indicando os espaços reaproveitveis
                    
                    // Se a pgina for folha, copia o ponteiro para a prxima pgina
                    pFilho.proxima = pIrmao.proxima;
                    
                    // puxa os registros no pai
                    for(int j=diminuido; j<pa.n-1; j++) {
                        pa.chaves[j] = pa.chaves[j+1];
                        pa.dados[j] = pa.dados[j+1];
                        pa.filhos[j+1] = pa.filhos[j+2];
                    }
                    pa.n--;
                    diminuiu = pa.n<maxElementos/2;  // testa se o pai tambm ficou sem o nmero mnimo de elementos
                }
            }
            
            // Atualiza todos os registros
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            arquivo.seek(paginaFilho);
            arquivo.write(pFilho.getBytes());
            arquivo.seek(paginaIrmao);
            arquivo.write(pIrmao.getBytes());
        }
        return excluido;
    }
    
    
    // Imprime a rvore, usando uma chamada recursiva.
    // A funçao recursiva  chamada com uma pgina de referência (raiz)
    public void print() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        if(raiz!=-1)
            print1(raiz);
        System.out.println();
    }
    
    // Impressao recursiva
    private void print1(long pagina) throws IOException {
        
        // Retorna das chamadas recursivas
        if(pagina==-1)
            return;
        int i;

        // Lê o registro da pgina passada como referência no arquivo
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
        
        // Imprime a pgina
        String endereco = String.format("%04d", pagina);
        System.out.print(endereco+"  " + pa.n +":"); // endereço e nmero de elementos
        for(i=0; i<maxElementos; i++) {
            System.out.print("("+String.format("%04d",pa.filhos[i])+") "+pa.chaves[i]+","+String.format("%2d",pa.dados[i])+" ");
        }
        System.out.print("("+String.format("%04d",pa.filhos[i])+")");
        if(pa.proxima==-1)
            System.out.println();
        else
            System.out.println(" --> ("+String.format("%04d", pa.proxima)+")");
        
        // Chama recursivamente cada filho, se a pgina nao for folha
        if(pa.filhos[0] != -1) {
            for(i=0; i<pa.n; i++)
                print1(pa.filhos[i]);
            print1(pa.filhos[i]);
        }
    }
       
    
    // Apaga o arquivo do ndice, para que possa ser reconstrudo
    public void apagar() throws IOException {

        File f = new File(nomeArquivo);
        f.delete();

        arquivo = new RandomAccessFile(nomeArquivo,"rw");
        arquivo.writeLong(-1);  // raiz empty
    }
    
}
