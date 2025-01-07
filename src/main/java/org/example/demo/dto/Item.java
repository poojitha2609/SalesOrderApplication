package org.example.demo.dto;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    private Long itemId;
    private String name;
    private Long itemNo;
    private String description;
    private float price;
    private int stockQty;
}
