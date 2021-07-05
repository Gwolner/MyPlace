package br.edu.ifpe.tads.pdm.myplaces.repositories;

import java.util.ArrayList;

import br.edu.ifpe.tads.pdm.myplaces.entities.AvaliacoesLocal;
import br.edu.ifpe.tads.pdm.myplaces.entities.CategoriasLocal;
import br.edu.ifpe.tads.pdm.myplaces.entities.Local;

public class LocalRepository {
    private ArrayList<Local> locais = new ArrayList<Local>();
    private static LocalRepository instanceLocalRepository;

    private LocalRepository(){
        //alimentação do banco com alguns locais
        Local aldeiaDoPapaiNoelGramado = new Local(
                "Aldeia do Papai Noel",
                "-29.37",
                "-50.90",
                "Gramado",
                "Rio Grande do Sul",
                5.0f,
                "Lugar Maravilhoso.",
                CategoriasLocal.PARQUE
                );
        Local florybalChocolatesGramado  = new Local(
                "Florybal Chocolates - A Fábrica Mágica",
                "-29.37",
                "-50.88",
                "Gramado",
                "Rio Grande do Sul",
                5.0f,
                "Voltar para comer outras opções de chocolates.",
                CategoriasLocal.LOJA
        );
        this.adicionarLocal(aldeiaDoPapaiNoelGramado);
        this.adicionarLocal(florybalChocolatesGramado);

    }

    public static LocalRepository getInstance() {
        if (instanceLocalRepository == null) {
            instanceLocalRepository = new LocalRepository();
        }
        return instanceLocalRepository;
    }

    public ArrayList<Local> listarLocais(){
        return this.locais;
    }

    public void adicionarLocal(Local local){
        locais.add(local);
    }

    public Local pegarLocal(String lat, String lng){
        for(Local l: this.locais){
            if(l.getLat().equals(lat) && l.getLng().equals(lng)){
                return l;
            }
        }
        return null;
    }

    public void deletarLocal(String lat, String lng){
        this.locais.remove(pegarLocal(lat, lng));
    }

    public void atualizarLocal(Local novosDadosLocal, String lat, String lng){
        this.locais.remove(pegarLocal(lat, lng));
        this.locais.add(novosDadosLocal);
    }

}
