package ru.polytech.cloudimagerepository.mapper;

import dev.brachtendorf.jimagehash.hash.Hash;
import org.bson.Document;

import java.math.BigInteger;

public class HashMapper {

    public static Hash toHash(Document document) {
        return new Hash(new BigInteger(document.getString("hashValue")),
                document.getInteger("hashLength"),
                document.getInteger("hashAlgorithmId"));
    }
}
