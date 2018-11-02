package io.examples.graphql.service;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * @author Gary Cheng
 */
@Service
public class GraphQLService {
    @Value("classpath:/graphql/movie.graphql")
    private Resource resource;
    @Autowired
    private FindAllDataFetcher findAllDataFetcher;
    @Autowired
    private FindByIdDataFetcher findByIdDataFetcher;
    @Autowired
    private FindByTitleDataFetcher findByTitleDataFetcher;

    private GraphQL graphQL;

    @PostConstruct
    private void loadSchema() throws IOException {
        File schemaFile = resource.getFile();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("all", findAllDataFetcher)
                        .dataFetcher("byId", findByIdDataFetcher)
                        .dataFetcher("byTitle", findByTitleDataFetcher)
                )
                .build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }
}
