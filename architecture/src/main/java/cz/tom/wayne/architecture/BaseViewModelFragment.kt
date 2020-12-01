package cz.tom.wayne.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

/**
 * Base Fragment that all fragment implementations using a [BaseViewModel] should override.
 */
abstract class BaseViewModelFragment<VM : BaseViewModel> : Fragment() {

    private var _viewModel: VM? = null

    protected val viewModel: VM
        get() = _viewModel ?: throw IllegalStateException("Illegal attempt to access the ViewModel when the fragment is not attached to view.")

    protected abstract val layoutId: Int

    abstract fun initViewModel(): VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewModel = initViewModel()
        val root = inflater.inflate(layoutId, container, false)
        inflateAdditionalViews(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beforeUi()
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewModel = null
    }

    protected open fun initUi() {
        // Children should override and init UI here.
    }

    // children base fragments can do some operations here before initUI is done on child fragments
    @CallSuper
    protected open fun beforeUi() {
    }

    /**
     * The child fragment can override this if it needs to inflate additional views after the main layout is inflated.
     * This is called in [onCreateView], after [initViewModel] and root view inflation.
     */
    protected open fun inflateAdditionalViews(root: View) {}
}
