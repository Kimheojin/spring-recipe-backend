package com.HeoJin.RecipeSearchEngine.autocomplete.repository;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public class AutocompleteRepositoryImpl implements AutocompleteRepository {


}
