package com.f1.utils.structs;

public abstract class AbstractDoubleLinkedListEntry implements DoubleLinkedListEntry<AbstractDoubleLinkedListEntry> {

	private AbstractDoubleLinkedListEntry prior;
	private AbstractDoubleLinkedListEntry next;

	@Override
	public AbstractDoubleLinkedListEntry getNext() {
		return this.next;
	}

	@Override
	public void setNext(AbstractDoubleLinkedListEntry next) {
		this.next = next;
	}

	@Override
	public AbstractDoubleLinkedListEntry getPrior() {
		return prior;
	}

	@Override
	public void setPrior(AbstractDoubleLinkedListEntry prior) {
		this.prior = prior;
	}

	@Override
	public void reset(AbstractDoubleLinkedListEntry prior, AbstractDoubleLinkedListEntry next) {
		this.prior = prior;
		this.next = next;
	}

}
