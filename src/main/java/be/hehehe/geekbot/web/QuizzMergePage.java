package be.hehehe.geekbot.web;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import be.hehehe.geekbot.persistence.dao.QuizzDAO;
import be.hehehe.geekbot.persistence.dao.QuizzMergeDAO;
import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;
import be.hehehe.geekbot.web.components.ChosenBehavior;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class QuizzMergePage extends TemplatePage {

	@Inject
	QuizzDAO quizzDAO;

	@Inject
	QuizzMergeDAO mergeDAO;

	public QuizzMergePage() {
		add(new SubmitForm("form"));
		add(new MergeForm("merge-form"));
	}

	private class SubmitForm extends Form<List<QuizzPlayer>> {

		private IModel<String> giver = new Model<String>();
		private IModel<String> receiver = new Model<String>();

		FeedbackPanel messages = new FeedbackPanel("messages");

		public SubmitForm(String id) {
			super(id);
			messages.setVisible(false);

			IModel<List<String>> model = new LoadableDetachableModel<List<String>>() {
				@Override
				protected List<String> load() {
					return Lists.transform(quizzDAO.getPlayersOrderByName(),
							new Function<QuizzPlayer, String>() {
								@Override
								public String apply(QuizzPlayer input) {
									return input.getName();
								}
							});
				}
			};

			add(new Button("submit-button"));
			DropDownChoice<String> giverChoice = new DropDownChoice<String>(
					"giver", giver, model);
			giverChoice.add(new ChosenBehavior());
			add(giverChoice);

			DropDownChoice<String> receiverChoice = new DropDownChoice<String>(
					"receiver", receiver, model);
			receiverChoice.add(new ChosenBehavior());
			add(receiverChoice);

			add(messages);
		}

		@Override
		protected void onSubmit() {
			try {
				mergeDAO.add(receiver.getObject(), giver.getObject());
				setResponsePage(QuizzMergePage.class);
			} catch (QuizzMergeException e) {
				messages.setVisible(true);
				error(e.getMessage());
			}
		}

	}

	private class MergeForm extends Form<List<QuizzMergeRequest>> {

		public MergeForm(String id) {
			super(id);

			IModel<List<QuizzMergeRequest>> model = new LoadableDetachableModel<List<QuizzMergeRequest>>() {
				@Override
				protected List<QuizzMergeRequest> load() {
					return mergeDAO.findAll();
				}
			};

			ListView<QuizzMergeRequest> requestsView = new PropertyListView<QuizzMergeRequest>(
					"requests", model) {
				@Override
				protected void populateItem(ListItem<QuizzMergeRequest> item) {
					QuizzMergeRequest request = item.getModelObject();
					final Long requestId = request.getId();
					item.add(new Label("receiving", request.getReceiver()));
					item.add(new Label("giving", request.getGiver()));

					SubmitLink accept = new SubmitLink("accept") {
						@Override
						public void onSubmit() {
							mergeDAO.executeMerge(requestId);
							setResponsePage(QuizzMergePage.class);
						}
					};

					SubmitLink deny = new SubmitLink("deny") {
						@Override
						public void onSubmit() {
							mergeDAO.deleteById(requestId);
							setResponsePage(QuizzMergePage.class);
						}
					};

					boolean hasRole = getAuthSession().getRoles().contains(
							Roles.ADMIN);
					accept.setVisible(hasRole);
					deny.setVisible(hasRole);

					item.add(accept);
					item.add(deny);
				}
			};
			requestsView.setReuseItems(true);
			add(requestsView);
		}
	}

	@Override
	protected String getTitle() {
		return "Quizz Merge Requests";
	}

}
