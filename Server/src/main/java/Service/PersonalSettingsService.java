package Service;

import DAO.PersonalSettingsDao;
import Models.Entities.PersonalSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PersonalSettingsService implements Service<PersonalSettings> {

    private final PersonalSettingsDao personalSettingsDao;

    public PersonalSettingsService() throws SQLException {
        this.personalSettingsDao = new PersonalSettingsDao();
    }

    @Override
    public void createEntity(PersonalSettings settings) throws SQLException {
        personalSettingsDao.add(settings);
    }

    @Override
    public Optional<PersonalSettings> getEntityById(int id) throws SQLException {
        return personalSettingsDao.findById(id);
    }

    @Override
    public void updateEntity(PersonalSettings settings) throws SQLException {
        personalSettingsDao.update(settings);
    }

    @Override
    public void deleteEntity(PersonalSettings settings) throws SQLException {
        personalSettingsDao.delete(settings);
    }

    @Override
    public List<PersonalSettings> getAllEntities() throws SQLException{
        return personalSettingsDao.findAll();
    }
}

