package ebookstore.service.implement;

import ebookstore.exception.ClientNotFoundException;
import ebookstore.exception.message.ClientErrorMessages;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.service.ClientService;
import ebookstore.service.csv.writer.ClientCsvExporter;

import java.util.Collection;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientCsvExporter clientCsvExporter;

    public ClientServiceImpl(ClientRepository clientRepository,
                             ClientCsvExporter clientCsvExporter) {
        this.clientRepository = clientRepository;
        this.clientCsvExporter = clientCsvExporter;
    }

    @Override
    public Client saveClient(Client client) {
        Client newClient = clientRepository.saveClient(client);

        return newClient;
    }

    @Override
    public Collection<Client> getAllClients() {

        return clientRepository.getAllClients().values();
    }

    @Override
    public Client getClientById(long clientId) {

        return clientRepository.getClient(clientId)
                .orElseThrow(() -> new ClientNotFoundException(ClientErrorMessages.FIND_ERROR));
    }

    @Override
    public Client updateClient(Client client) {
        Client oldClient = getClientById(client.getId());
        Client newClient = clientRepository.updateClient(oldClient);

        return newClient;
    }

    @Override
    public void deleteClientById(long clientId) {
        Client client = getClientById(clientId);
        clientRepository.deleteClient(client.getId());
    }

    @Override
    public boolean checkClientIsExist(long clientId) {
        return clientRepository.checkClientIsExist(clientId);
    }

    @Override
    public void exportClientsToCsv(String filePath) {
        Collection<Client> allClients = clientRepository.getAllClients().values();
        clientCsvExporter.exportToCsv(allClients, filePath);
    }
}
