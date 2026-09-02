package __24529.projeto2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table
@Data
@Getter
@Setter
@ToString
public class Usuario
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String cpf;

    @Column
    private String nome;

    @Column
    private LocalDate dataAniversario;

    @Column
    private String celular;

    @Column
    private String email;

    @Column
    private String senha;
}
