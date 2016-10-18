package com.n26.codechallenge;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * An immutable representation of a Transaction
 */
@ToString
@EqualsAndHashCode
public class Transaction {

    private static final ObjectMapper mapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Min(value =  0, message = "amount cannot be negative")
    @NotNull(message = "amount cannot be null")
    private final Double amount;

    @NotNull(message = "type cannot be null")
    private final String type;

    @Min(value =  0, message = "parent_id cannot be negative")
    private final Long parentId;

    /**
     * This constructor is used by Jackson to build an instance from its JSON representation.
     *
     * @param amount
     * @param type
     * @param parentId
     */
    @JsonCreator
    public Transaction (@JsonProperty("amount") Double amount, @JsonProperty("type") String type, @JsonProperty("parent_id") Long parentId){
        this.amount = amount;
        this.type = type;
        this.parentId = parentId;
    }

    public Transaction (Double amount, String type){
        this(amount, type, null);
    }

    /**
     * Exposes the amount of this transaction
     * @return
     */
    public Double amount(){
        return amount;
    }

    /**
     * Checks whether this transaction has a given type
     * @param type
     * @return
     */
    public boolean hasType(String type){
        return this.type.equals(type);
    }

    /**
     * Checks whether this transaction is child of transaction, given its unique identifier
     * @param parentId
     * @return
     */
    public boolean isChild(Long parentId) {
        return this.parentId != null && this.parentId.equals(parentId);
    }

    /**
     * Exposes the unique identifier of the parent transaction of this
     * @return
     */
    public Long parentId() {
        return parentId;
    }

    /**
     * Exposes the type of the transaction
     * @return
     */
    public String type(){
        return type;
    }

    /**
     * Builds a JSON representation of this model
     * @return
     * @throws JsonProcessingException
     */
    public String toJson() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

}
