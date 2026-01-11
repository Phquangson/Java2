package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import entities.Notification;

public class NotificationModel {
	public List<Notification> findAllNotification() {
		List<Notification> notifications = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("""
					    SELECT n._id, n._title, n._content, n._id_creator, n._id_updater,
					           n._created_date, n._updated_date,
					           n._id_status, s._title AS status_title
					    FROM tbl_notification n
					    LEFT JOIN tbl_status s ON n._id_status = s._id
					    ORDER BY n._created_date DESC;
					""");

			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Notification notification = new Notification();
				notification.setId(resultSet.getInt("_id"));
				notification.setTitle(resultSet.getString("_title"));
				notification.setContent(resultSet.getString("_content"));
				notification.setIdCreator(resultSet.getString("_id_creator"));
				notification.setIdUpdater(resultSet.getString("_id_updater"));
				notification.setCreatedDate(resultSet.getTimestamp("_created_date"));
				notification.setUpdatedDate(resultSet.getTimestamp("_updated_date"));

				notification.setIdStatus(resultSet.getInt("_id_status"));
				notification.setStatusTitle(resultSet.getString("status_title"));

				notifications.add(notification);
			}

		} catch (Exception e) {
			e.printStackTrace();
			notifications = null;
		} finally {
			ConnectDB.disconnect();
		}
		return notifications;
	}

	public boolean insertNotification(Notification notification) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement(
					"""
							    INSERT INTO tbl_notification(_title, _content, _id_creator, _id_status, _created_date, _id_updater)
							    VALUES (?, ?, ?, 13, CURRENT_TIMESTAMP, ? );
							""");
			preparedStatement.setString(1, notification.getTitle());
			preparedStatement.setString(2, notification.getContent());
			preparedStatement.setString(3, notification.getIdCreator());

			String updater = notification.getIdUpdater() != null ? notification.getIdUpdater() : "staff";
			preparedStatement.setString(4, updater);

			int rows = preparedStatement.executeUpdate();
			result = rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return result;
	}

	public boolean updateStatus(int notificationId, int newStatusId) {
		boolean result = false;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("""
					    UPDATE tbl_notification
					    SET _id_status = ?, _updated_date = CURRENT_TIMESTAMP
					    WHERE _id = ?;
					""");
			preparedStatement.setInt(1, newStatusId);
			preparedStatement.setInt(2, notificationId);

			int rows = preparedStatement.executeUpdate();
			result = rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return result;
	}

}
