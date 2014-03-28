package model.core;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import model.base.Identifiable;
import model.factories.TermFactory;
import model.repositories.TermRepository;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.google.common.collect.ImmutableList;

@Entity
public class Analysis extends Model implements Identifiable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  @Required
  @ManyToOne
  private final Customer owner;

  @Required
  private String name;

  @ManyToMany
  private final List<Term> terms = new ArrayList<Term>();

  public Analysis(final Customer owner, final String name) {
    this.owner = owner;
  }

  @Override
  public Long getId() {
    return id;
  }

  public Customer getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<Term> getTerms() {
    return ImmutableList.copyOf(terms);
  }

  public void addTerm(final String content) {
    Term term = TermRepository.INSTANCE.one(content);
    if (term == null) {
      term = TermFactory.INSTANCE.create(content);
      TermRepository.INSTANCE.store(term);
      term.refresh();
    }
    terms.add(term);
    this.save();
  }

  public void removeTerm(final Term term) {
    terms.remove(term);
    this.save();
  }

  @Override
  public void save() {
    for (final Term term : terms) {
      term.save();
    }
    super.save();
  }

  @Override
  public void delete() {
    for (final Term term : terms) {
      term.delete();
    }
    super.delete();
  }

}
